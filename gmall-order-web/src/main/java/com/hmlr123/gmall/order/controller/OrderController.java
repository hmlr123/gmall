package com.hmlr123.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.annotations.LoginRequired;
import com.hmlr123.gmall.bean.OmsCartItem;
import com.hmlr123.gmall.bean.OmsOrder;
import com.hmlr123.gmall.bean.OmsOrderItem;
import com.hmlr123.gmall.bean.UmsMemberReceiveAddress;
import com.hmlr123.gmall.service.CartService;
import com.hmlr123.gmall.service.OrderService;
import com.hmlr123.gmall.service.SkuService;
import com.hmlr123.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 订单.
 *
 * @author liwei
 * @date 2019/9/14 14:29
 */
@Controller
public class OrderController {

    @Reference
    private CartService cartService;

    @Reference
    private UserService userService;

    @Reference
    private OrderService orderService;

    @Reference
    private SkuService skuService;

    /**
     * 结算.
     * 跳转到结算界面
     *
     * @return
     */
    @LoginRequired(loginSuccess = true)
    @RequestMapping("toTrade")
    private String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {

        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        //收件人列表
        List<UmsMemberReceiveAddress> receiveAddressList = userService.getReceiveAddressByMemeberId(memberId);

        List<OmsCartItem> omsCartItemList = cartService.cartList(memberId);

        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for (OmsCartItem cartItem : omsCartItemList) {
            if ("1".equals(cartItem.getIsChecked())) {
                OmsOrderItem orderItem = new OmsOrderItem();
                orderItem.setProductPrice(cartItem.getPrice());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductPic(cartItem.getProductPic());
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setProductBrand(cartItem.getProductBrand());
                orderItem.setProductQuantity(cartItem.getQuantity());
                orderItem.setProductSkuId(cartItem.getProductSkuId());
                orderItem.setProductSkuCode(cartItem.getProductSkuCode());
                orderItem.setProductCategoryId(cartItem.getProductCategoryId());
                orderItem.setSp1(cartItem.getSp1());
                orderItem.setSp2(cartItem.getSp2());
                orderItem.setSp3(cartItem.getSp3());
                orderItem.setProductAttr(cartItem.getProductAttr());
                omsOrderItemList.add(orderItem);
            }
        }
        String tradeCode = orderService.generalTradeCode(memberId);
        modelMap.put("userAddressList", receiveAddressList);
        modelMap.put("orderDetailList", omsOrderItemList);
        modelMap.put("totalAmount", getTotalAmount(omsOrderItemList));
        modelMap.put("tradeCode", tradeCode);
        return "trade";
    }


    /**
     * 提交订单.
     * 1. 检查订单数据
     * 2. 生成订单数据
     * 3. 删除购物车记录
     * 4. 跳转到支付界面
     *
     * @return
     */
    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode,
                                    HttpServletRequest request, HttpServletResponse response) {
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        //检测交易码，防止循环跳转重复提交订单
        String isTradeCode = orderService.checkTradeCode(memberId, tradeCode);
        if ("success".equals(isTradeCode)) {
            List<OmsOrderItem> omsOrderItemList = new ArrayList<>();

            //封装订单数据
            OmsOrder order = new OmsOrder();
            //创建时间
            order.setCreateTime(new Date());
            //用户名
            order.setMemberUsername(nickname);
            order.setNote("等不及了");
            //订单编号 外部使用 比如支付
            String outTrandeNo = new SimpleDateFormat("YYYYMMDDHHmmss").format(new Date());
            outTrandeNo = outTrandeNo + System.currentTimeMillis();//时间戳
            order.setOrderSn(outTrandeNo);
            //总金额
            order.setTotalAmount(totalAmount);
            //支付方式 0未支付 1支付宝 2微信
            order.setPayType(0);
            //订单来源 0 PC订单 1 app订单
            order.setSourceType(0);
            //订单状态 0 待付款 1代发货 2已发货 3已完成 4已关闭 5无效订单
            order.setStatus(0);
            //订单类型 0 正常 1秒杀
            order.setOrderType(0);
            //收货时间 当前时间加一天 一天后配送
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            order.setReceiveTime(calendar.getTime());
            order.setDeleteStatus(0);

            //收货相关
            //获取收获地址数据
            UmsMemberReceiveAddress address = userService.getReceiveAddressById(receiveAddressId);
            order.setReceiverCity(address.getCity());
            order.setReceiverDetailAddress(address.getDetailAddress());
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhoneNumber());
            order.setReceiverPostCode(address.getPostCode());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverRegion(address.getRegion());
            order.setMemberId(memberId);
            //折扣
            order.setDiscountAmount(null);



            //获取订单数据
            List<OmsCartItem> omsCartItemList = cartService.cartList(memberId);
            for (OmsCartItem omsCartItem : omsCartItemList) {
                OmsOrderItem orderItem = new OmsOrderItem();

                //检查价格
                boolean isPrice = skuService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                //价格有变动
                if (!isPrice) {
                    return new ModelAndView("tradeFail");
                }

                //检验库存系统 远程调用库存系统


                //封装相关数据
                orderItem.setProductPrice(omsCartItem.getPrice());
                orderItem.setOrderSn(outTrandeNo); //外部单号
                orderItem.setProductId(omsCartItem.getProductId());
                orderItem.setProductPic(omsCartItem.getProductPic());
                orderItem.setProductName(omsCartItem.getProductName());
                orderItem.setProductBrand(omsCartItem.getProductBrand());
                orderItem.setProductQuantity(omsCartItem.getQuantity());
                orderItem.setProductSkuId(omsCartItem.getProductSkuId());
                orderItem.setProductSkuCode(omsCartItem.getProductSkuCode());
                orderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                orderItem.setSp1(omsCartItem.getSp1());
                orderItem.setSp2(omsCartItem.getSp2());
                orderItem.setSp3(omsCartItem.getSp3());
                orderItem.setProductAttr(omsCartItem.getProductAttr());
                orderItem.setProductSn("仓库对应的商品编号"); // 在仓库中的skuId
                orderItem.setRealAmount(omsCartItem.getTotalPrice());

                omsOrderItemList.add(orderItem);

            }

            order.setOmsOrderItemList(omsOrderItemList);
            //订单数据写入数据库 删除购物车记录
            orderService.saveOrder(order);

            //重定向到支付系统
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.gmall.hmlr123.com/index");
            modelAndView.addObject("outTradeNo", outTrandeNo);
            modelAndView.addObject("totalAmount", totalAmount);
            return modelAndView;

        } else {
            return new ModelAndView("tradeFail");
        }
    }


    /**
     * 计算总价格.
     *
     * @param omsOrderItems
     */
    private BigDecimal getTotalAmount(List<OmsOrderItem> omsOrderItems) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : omsOrderItems) {
            totalAmount = totalAmount.add(orderItem.getProductPrice().multiply(new BigDecimal(orderItem.getProductQuantity())));
        }
        return totalAmount;
    }
}
