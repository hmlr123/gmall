package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: SkuService
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/24 17:36
 * @Version: 1.0
 */
public interface SkuService {

    /**
     * 保存SKU数据.
     *
     * @param pmsSkuInfo    SKU数据
     */
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 获取sku数据.
     *
     * @param skuId sku数据
     * @return      sku实体数据
     */
    PmsSkuInfo getBySkuId(String skuId, String ip);

    /**
     * 获取其他sku数据.
     *
     * @param productId     一类产品id
     * @return              sku集合
     */
    List<PmsSkuInfo> getPmsSkuSaleAttrValueByProductId(String productId);

    /**
     * 从数据库获取sku数据.
     *
     * @param skuId sku数据
     * @return      sku实体数据
     */
    PmsSkuInfo getBySkuIdFromDb(String skuId);

    /**
     * 根据三级分类id获取所有sku数据。
     *
     * @param catalog3Id    三类分级id
     * @return              PmsSkuInfo数据集合
     */
    List<PmsSkuInfo> getAllSkuInfo(String catalog3Id);

    /**
     * 检查价格.
     *
     * @param productSkuId  skuId
     * @param price         价格
     * @return
     */
    boolean checkPrice(String productSkuId, BigDecimal price);
}
