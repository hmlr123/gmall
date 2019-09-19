package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PmsSearchParam;
import com.hmlr123.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

/**
 * 搜索服务.
 *
 * @author liwei
 * @date 2019/9/8 16:35
 */
public interface SearchService {

    /**
     * 搜索服务,根据搜索条件从es获取数据.
     *
     * @param pmsSearchParam    搜索条件 三级分类id 关键字 平台属性值id集合
     * @return
     */
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
