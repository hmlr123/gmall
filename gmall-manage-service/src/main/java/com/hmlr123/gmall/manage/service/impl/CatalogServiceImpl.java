package com.hmlr123.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hmlr123.gmall.bean.PmsBaseCatalog1;
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
@Service//RPC的
public class getCatalogImpl implements CatalogService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    /**
     * 获取目录一的数据
     * @return
     */
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }
}
