package com.hmlr123.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.PmsBaseCatalog1;
import com.hmlr123.gmall.bean.PmsBaseCatalog2;
import com.hmlr123.gmall.bean.PmsBaseCatalog3;
import com.hmlr123.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName: CatalogController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/3 19:56
 * @Version: 1.0
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class CatalogController {

    @Reference
    CatalogService catalogService;

    /**
     * 获取目录一的数据
     * @return
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1() {
        return catalogService.getCatalog1();
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        return catalogService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        return catalogService.getCatalog3(catalog2Id);
    }
}
