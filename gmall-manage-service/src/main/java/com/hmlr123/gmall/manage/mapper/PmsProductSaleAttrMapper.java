package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsBaseAttrValue;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsProductSaleAttrMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/18 20:37
 * @Version: 1.0
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    /**
     * 批量保存商品属性数据.
     * @param pmsProductSaleAttrs
     */
    void batchInsertPmsProductSaleAttr(List<PmsProductSaleAttr> pmsProductSaleAttrs);

    /**
     * 查询
     * @param productId
     * @param skuId
     * @return
     */
    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("productId") String productId,
                                                             @Param("skuId") String skuId);
}
