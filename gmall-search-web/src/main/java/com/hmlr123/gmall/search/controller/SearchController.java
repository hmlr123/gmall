package com.hmlr123.gmall.search.controller;

import com.hmlr123.gmall.annotations.LoginRequired;
import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.*;
import com.hmlr123.gmall.service.AttributeService;
import com.hmlr123.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * 文本搜索.
 *
 * @author liwei
 * @date 2019/9/8 15:53
 */
@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttributeService attributeService;

    /**
     * 跳转主页.
     *
     * @return  index
     */
    @RequestMapping("index")
    @LoginRequired(loginSuccess = false) //为了cookie保存
    public String index(String returnUrl, ModelMap modelMap) {
        modelMap.put("returnUrl", returnUrl);
        return "index";
    }

    /**
     * 跳转.
     * 三级分类id 关键字 平台属性集合
     *
     * @param pmsSearchParam    搜索数据模型
     * @return
     */
    @RequestMapping("list.html")
    @LoginRequired(loginSuccess = false)
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        //搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfos =  searchService.list(pmsSearchParam);

        //获取valueId 去重
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }

        //根据valueId查询 属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = attributeService.getAttrValueListByValueId(valueIdSet);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);



//         两种面包屑 1. 平台销售属性
//        2. 用户已经选择的销售属性url
        List<PmsSearchCrumb> pmsSearchCrumbList = new ArrayList<>();
        //清洗urlParam 平台销售属性 及属性值 生成面包屑
        String urlParam = getUrlParam(pmsSearchParam, pmsBaseAttrInfoList, pmsSearchCrumbList);
        //可选平台属性
        modelMap.put("attrList", pmsBaseAttrInfoList);

        //请求参数
        modelMap.put("urlParam", urlParam);

        //已选平台属性
        modelMap.put("attrValueSelectedList", pmsSearchCrumbList);

        //关键字
        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            modelMap.put("keyword", keyword);
        }

        return "list";
    }

    /**
     * 清洗urlParam、平台销售属性.
     * 生成面包屑.
     *
     * @param pmsSearchParam        请求参数 面包屑id数组 关键字 三级分类id
     * @param pmsBaseAttrInfoList   平台销售属性集合
     * @param pmsSearchCrumbList    面包屑
     * @return
     */
    private String getUrlParam(PmsSearchParam pmsSearchParam,
                               List<PmsBaseAttrInfo> pmsBaseAttrInfoList,
                               List<PmsSearchCrumb> pmsSearchCrumbList) {
        //返回数据中去除已经请求过的数据
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        //属性值id数组 应该去除的属性值Id（反向找属性id）
        String[] valueIds = pmsSearchParam.getValueId();

        String param = "";

        //拼接catalog3Id
        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(param)) {
                param = param + "&";
            }
            param = param + "catalog3Id=" + catalog3Id;
        }
        //拼接keyword
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(keyword)) {
                param = param + "&";
            }
            param = param + "keyword=" + keyword;
        }

        //区别
        String urlParam = param;
        if (null != valueIds) {
            for (String valueId : valueIds) {
                //生成没有选择的面包屑的urlParam
                urlParam = "&valueId=" + valueId;
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String pmsBaseAttrValueId = pmsBaseAttrValue.getId();
                        if (valueId.equals(pmsBaseAttrValueId)) {
                            String urlParamSelected = param;
                            PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                            pmsSearchCrumb.setValueName(pmsBaseAttrInfo.getAttrName() + ":" + pmsBaseAttrValue.getValueName());
                            pmsSearchCrumb.setValueId(pmsBaseAttrValueId);
                            //去除当前已经选择过的valueId 生成已经选择过的urlParam 过滤当前valueId
                            for (String id : valueIds) {
                                if (!valueId.equals(id)) {
                                    urlParamSelected = "&valueId=" +id;
                                }
                            }
                            pmsSearchCrumb.setUrlParam(urlParamSelected);
                            pmsSearchCrumbList.add(pmsSearchCrumb);
                            //删除已经选择过的面包屑
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return urlParam;
    }
}
