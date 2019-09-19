package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PmsBaseCatalog1;
import com.hmlr123.gmall.bean.PmsBaseCatalog2;
import com.hmlr123.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @ClassName: CatalogService
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/3 20:22
 * @Version: 1.0
 */
public interface CatalogService {

    /**
     * 获取目录一的数据.
     * @return  目录一的数据实体
     */
    List<PmsBaseCatalog1> getCatalog1();

    /**
     * 获取目录2的数据.
     * @param catalog1Id    目录一的id
     * @return              目录二的数据实体
     */
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 获取目录3的数据.
     * @param catalog2Id    目录二的id
     * @return              目录三的数据实体
     */
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
