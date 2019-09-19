package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsProductImage;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsProductImageMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/18 20:52
 * @Version: 1.0
 */
public interface PmsProductImageMapper extends Mapper<PmsProductImage> {

    /**
     * 批量保存商品图片路径.
     * @param pmsProductImages
     */
    void batchInsertPmsProductImage(List<PmsProductImage> pmsProductImages);
}
