package com.hmlr123.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hmlr123.gmall.bean.PmsBaseCatalog1;
import com.hmlr123.gmall.bean.PmsBaseCatalog2;
import com.hmlr123.gmall.bean.PmsBaseCatalog3;
import com.hmlr123.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.hmlr123.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.hmlr123.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.hmlr123.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName: getCatalogImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/3 20:24
 * @Version: 1.0
 */
@Service(
        timeout = 1200000
)//RPC的
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    /**
     * 获取目录一的数据.
     * @return  目录一的数据实体
     */
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }

    /**
     * 获取目录2的数据.
     * @param catalog1Id    目录一的id
     * @return              目录二的数据实体
     */
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        return pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
    }

    /**
     * 获取目录3的数据.
     * @param catalog2Id    目录二的id
     * @return              目录三的数据实体
     */
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        return pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
    }
}
