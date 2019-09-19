package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PmsBaseAttrInfo;
import com.hmlr123.gmall.bean.PmsBaseAttrValue;
import com.hmlr123.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: AttributeService
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 0:32
 * @Version: 1.0
 */
public interface AttributeService {

    /**
     * 根据商品id获取系列商品的平台属性.
     * @param catalog3Id    商品分类id
     * @return              商品属性列表
     */
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    /**
     * 保存商品的属性和属性值.
     * @param pmsBaseAttrInfo   商品属性信息尸体 属性名 属性值 1:n
     */
    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 根据属性id获取属性值
     * @param attrId    属性id
     * @return          属性值实体集合
     */
    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    /**
     * 获取平台销售属性字典表数据
     * @return
     */
    List<PmsBaseSaleAttr> baseSaleAttrList();

    /**
     * 获取平台属性集合
     *
     * @param valueIdSet    平台属性值id
     * @return              平台属性集合
     */
    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet);


    /**
     * 获取平台属性全部数据.
     *
     * @return 平台属性集合
     */
    List<PmsBaseAttrInfo> getAllAttrValueList();
}
