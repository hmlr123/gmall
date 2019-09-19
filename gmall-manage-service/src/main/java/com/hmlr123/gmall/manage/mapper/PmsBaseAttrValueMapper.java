package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsBaseAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsBaseAttrValueMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 0:36
 * @Version: 1.0
 */
public interface PmsBaseAttrValueMapper extends Mapper<PmsBaseAttrValue> {
    /**
     * 批量保存PmsBaseAttrValue数据.
     * @param attrValueList 系列商品属性值
     */
    void batchInsertPmsBaseAttrValue(List<PmsBaseAttrValue> attrValueList);
}
