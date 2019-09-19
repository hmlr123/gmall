package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsSkuInfoMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/24 20:28
 * @Version: 1.0
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    /**
     * 获取其他sku数据.
     *
     * @param productId     一类产品id
     * @return              sku集合
     */
    List<PmsSkuInfo> getPmsSkuSaleAttrValueByProductId(@Param("productId") String productId);
}
