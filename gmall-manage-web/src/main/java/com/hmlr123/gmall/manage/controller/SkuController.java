package com.hmlr123.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.PmsSkuInfo;
import com.hmlr123.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: SkuController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/24 17:32
 * @Version: 1.0
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class SkuController {

    @Reference
    private SkuService skuService;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
