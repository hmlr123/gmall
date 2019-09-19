package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import com.hmlr123.gmall.bean.PmsProductSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsProductSaleAttrValueMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/18 20:38
 * @Version: 1.0
 */
public interface PmsProductSaleAttrValueMapper extends Mapper<PmsProductSaleAttrValue> {

    /**
     * 批量保存商品属性值数据.
     * @param pmsProductSaleAttrValues
     */
    void batchInsertPmsProductSaleAttrValue(List<PmsProductSaleAttrValue> pmsProductSaleAttrValues);
}
