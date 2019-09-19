package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PmsBaseSaleAttr;
import com.hmlr123.gmall.bean.PmsProductImage;
import com.hmlr123.gmall.bean.PmsProductInfo;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @ClassName: SpuService
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 20:21
 * @Version: 1.0
 */
public interface SpuService {
    /**
     * 获取spu数据.
     * @param catalog3Id
     * @return
     */
    List<PmsProductInfo> spuList(String catalog3Id);

    /**
     * 保存spu数据
     * @param pmsProductInfo
     * @return
     */
    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    /**
     * 通过SPUID获取商品属性名称.
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    /**
     * 获取销售商品的图片.
     * @param spuId spuid
     * @return      销售图片集合
     */
    List<PmsProductImage> spuImageList(String spuId);


    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}
