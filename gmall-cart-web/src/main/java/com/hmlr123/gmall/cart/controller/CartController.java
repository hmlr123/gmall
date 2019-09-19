package com.hmlr123.gmall.cart.controller;

import com.hmlr123.gmall.annotations.LoginRequired;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hmlr123.gmall.bean.OmsCartItem;
import com.hmlr123.gmall.bean.PmsSkuInfo;
import com.hmlr123.gmall.bean.PmsSkuSaleAttrValue;
import com.hmlr123.gmall.service.CartService;
import com.hmlr123.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hmlr123.gmall.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 购物车控制类.
 *
 * @author liwei
 * @date 2019/9/9 21:01
 */
@Controller
public class CartController {


    @Reference
    private SkuService skuService;

    @Reference
    private CartService cartService;

    /**
     * 添加到购物车.
     *
     * @return
     */
    @RequestMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId,
                            Integer num,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        if (0 == num) {
            return "/error";
        }

        String ip = request.getRemoteHost();

        //获取商品详情
        PmsSkuInfo pmsSkuInfo = skuService.getBySkuId(skuId, ip);

        //封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        //购物车商品id
//        omsCartItem.setId("");
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        //用户id
        omsCartItem.setMemberId("");
        omsCartItem.setQuantity(num);
        //总共价格
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductSubTitle(pmsSkuInfo.getSkuDesc());
        //商品sku条码
        omsCartItem.setProductSkuCode("");
        //会员昵称
        omsCartItem.setMemberNickname("");
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        //商标
        omsCartItem.setProductBrand("");
        //跟库存相关
        omsCartItem.setProductSn("");
        //商品销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        String attrValue = "";
        if (null != skuSaleAttrValueList) {
            attrValue = JSONArray.toJSONString(skuSaleAttrValueList);
        }
        omsCartItem.setProductAttr(attrValue);
        //销售属性
        omsCartItem.setSp1("");
        omsCartItem.setSp2("");
        omsCartItem.setSp3("");


        //判断用户是否登录
        String memeberId = (String)request.getAttribute("memberId");
        List<OmsCartItem> omsCartItems = new ArrayList<>();

        //决定用户数据走cookie 还是走db
        if (StringUtils.isNotBlank(memeberId)) {
            //走db 已经登录，从数据库中将用户的购物车数据查询出来
            OmsCartItem omsCartItemFromDb = cartService.getCartByUserSkuId(memeberId, skuId);
            if (null == omsCartItemFromDb) {
                //添加到购物车
                omsCartItem.setMemberId(memeberId);
                cartService.addCart(omsCartItem);
            } else {
                //添加数量
                omsCartItemFromDb.setQuantity(omsCartItem.getQuantity() + omsCartItemFromDb.getQuantity());
                cartService.updateCart(omsCartItemFromDb);
            }

            //同步缓存
            cartService.flushCartCache(memeberId);

        } else {
            //走cookie 没有登录
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
            //去重或者添加数据
            dealDate(omsCartItems, omsCartItem, pmsSkuInfo);
            //更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie",
                    JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        }

        return "redirect:/success.html";
    }

    /**
     * 去重或者添加.
     *
     * @param omsCartItems  购物车数据
     * @param omsCartItem   比对数据
     * @param pmsSkuInfo    商品价格
     */
    private void dealDate(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem, PmsSkuInfo pmsSkuInfo) {
        boolean flag = true;
        for (OmsCartItem dto : omsCartItems) {
            // 判断是否存在相同的数据
            if (StringUtils.isNotBlank(omsCartItem.getProductSkuId())
                    && StringUtils.isNotBlank(dto.getProductSkuId())
                    && omsCartItem.getProductSkuId().equals(dto.getProductSkuId())) {
                dto.setQuantity(dto.getQuantity() + omsCartItem.getQuantity());
                flag = false;
            }
        }
        if (flag) {
            omsCartItems.add(omsCartItem);
        }
    }

    /**
     * 到购物车结算.
     *
     * @return
     */
    @RequestMapping("cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response ,ModelMap modelMap) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String userId = (String)request.getAttribute("memberId");
        if (StringUtils.isNotBlank(userId)) {
            // 获取用户购物车数据
            omsCartItems = cartService.cartList(userId);
        } else {
            //查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSONArray.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        //计算总数
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        }

        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("cartList", omsCartItems);
        return "cartList";
    }

    /**
     * AJAX异步刷新，后台渲染.
     *
     * @param isChecked     是否选中
     * @param skuId         商品ID
     * @param modelMap      模型数据
     * @return              跳转
     */
    @RequestMapping("checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(String isChecked, String skuId, ModelMap modelMap, HttpServletRequest request) {
        if (StringUtils.isNotBlank(isChecked) && StringUtils.isNotBlank(skuId)) {
            String memberId = (String)request.getAttribute("memberId");
            String nickname = (String)request.getAttribute("nickname");


            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(memberId);
            omsCartItem.setIsChecked(isChecked);
            omsCartItem.setProductSkuId(skuId);
            //修改购物车数据
            cartService.checkCart(omsCartItem);
            //获取购物车数据
            List<OmsCartItem> omsCartItemList = cartService.cartList(memberId);
            BigDecimal totalAmount = getTotalAmount(omsCartItemList);
            modelMap.put("totalAmount", totalAmount);
            modelMap.put("cartLIst", omsCartItemList);
        }

        return "cartListInner";
    }

    /**
     * 计算总价格.
     *
     * @param omsCartItems
     */
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (StringUtils.isNotBlank(omsCartItem.getIsChecked()) && "1".equals(omsCartItem.getIsChecked())) {
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }
}