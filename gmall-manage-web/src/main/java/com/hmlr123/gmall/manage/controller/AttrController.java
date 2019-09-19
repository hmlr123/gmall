package com.hmlr123.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.PmsBaseAttrInfo;
import com.hmlr123.gmall.bean.PmsBaseAttrValue;
import com.hmlr123.gmall.bean.PmsBaseSaleAttr;
import com.hmlr123.gmall.service.AttributeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName: AttrController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 0:28
 * @Version: 1.0
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class AttrController {

    @Reference
    private AttributeService attributeService;

    /**
     * 一级目录：商品大类.
     * 二级目录：商品细类
     * 三级目录：商品系列 ——> 此时还没有定位到商品，需要通过商品属性定位到某一系列的某一商品
     * 根据目录分类id3获取平台属性值
     * @param catalog3Id    目录分类id
     * @return              平台属性值
     */
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        return attributeService.attrInfoList(catalog3Id);
    }

    /**
     * 保存or修改属性和属性值.
     * @param pmsBaseAttrInfo   属性实体数据
     * @return                  成功标识
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attributeService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    /**
     * 根据属性id获取属性值
     * @param attrId
     * @return
     */
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        return attributeService.getAttrValueList(attrId);
    }

    /**
     * 商家在添加spu商品信息的时，添加销售属性（自定义）.
     * 获取平台销售属性字典表
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return attributeService.baseSaleAttrList();
    }
}
