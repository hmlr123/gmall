package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsProductInfo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName: PmsProductInfoMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 20:24
 * @Version: 1.0
 */
public interface PmsProductInfoMapper extends Mapper<PmsProductInfo> {

    /**
     * 保存spu数据
     * @param pmsProductInfo
     */
    void saveSpuInfo(PmsProductInfo pmsProductInfo);
}
