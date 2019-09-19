package com.hmlr123.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import com.hmlr123.gmall.bean.PmsSkuInfo;
import com.hmlr123.gmall.bean.PmsSkuSaleAttrValue;
import com.hmlr123.gmall.service.SkuService;
import com.hmlr123.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @date 2019/9/4 11:01
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class ItemControler {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;


    @RequestMapping("index")
    public String index(ModelMap modelMap) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("编号：" + i);
        }
        modelMap.put("list", list);
        modelMap.put("key", "Hello, World");
        modelMap.put("success", "明天会更好！");
        return "index";
    }

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        //sku对象
        PmsSkuInfo pmsSkuInfo = skuService.getBySkuId(skuId, ip);
        modelMap.put("skuInfo", pmsSkuInfo);

        //销售属性列表
        String productId = pmsSkuInfo.getProductId();
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(productId, pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        Map<String, String> valuesSku = new HashMap<>();

        //获取其他sku的属性集合 spu下的其他sku集合
        List<PmsSkuInfo> pmsSkuInfoList = skuService.getPmsSkuSaleAttrValueByProductId(pmsSkuInfo.getProductId());
        for (PmsSkuInfo dto : pmsSkuInfoList) {
            String value = dto.getId();
            String key = "";
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = dto.getSkuSaleAttrValueList();
            for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
                if (i > 0) {
                    key = key + "|";
                }
                key = key + skuSaleAttrValueList.get(i).getSaleAttrValueId();
            }
            valuesSku.put(key, value);
        }
        String toJSONString = JSON.toJSONString(valuesSku);
        modelMap.put("valuesSku", toJSONString);
        return "item";
    }

}
