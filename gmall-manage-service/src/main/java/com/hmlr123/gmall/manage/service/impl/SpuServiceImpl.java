package com.hmlr123.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hmlr123.gmall.bean.PmsProductImage;
import com.hmlr123.gmall.bean.PmsProductInfo;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import com.hmlr123.gmall.bean.PmsProductSaleAttrValue;
import com.hmlr123.gmall.manage.mapper.PmsProductImageMapper;
import com.hmlr123.gmall.manage.mapper.PmsProductInfoMapper;
import com.hmlr123.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.hmlr123.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.hmlr123.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SpuServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 20:22
 * @Version: 1.0
 */
@Service(
        timeout = 1200000
)
public class SpuServiceImpl implements SpuService {

    /**
     * 商品SPU.
     */
    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    /**
     * 商品属性数据.
     */
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    /**
     * 商品属性值数据.
     */
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    /**
     * 获取spu数据.
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        return pmsProductInfoMapper.select(pmsProductInfo);
    }

    /**
     * 保存spu数据
     * @param pmsProductInfo
     * @return
     */
    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        //保存商品SPU数据
        pmsProductInfoMapper.insert(pmsProductInfo);
        //保存商品属性数据 和商品销售属性值数据
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = new ArrayList<>();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            //保存商品ID
            pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
            for (PmsProductSaleAttrValue dto : pmsProductSaleAttr.getSpuSaleAttrValueList()) {
                //保存商品ID
                dto.setProductId(pmsProductInfo.getId());
                pmsProductSaleAttrValueList.add(dto);
            }
        }
        //批量保存商品销售属性数据
        pmsProductSaleAttrMapper.batchInsertPmsProductSaleAttr(spuSaleAttrList);
        //批量保存商品销售属性值数据
        pmsProductSaleAttrValueMapper.batchInsertPmsProductSaleAttrValue(pmsProductSaleAttrValueList);

        //保存SPU图片数据
        List<PmsProductImage> pmsProductImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : pmsProductImageList) {
            pmsProductImage.setProductId(pmsProductInfo.getId());
        }
        pmsProductImageMapper.batchInsertPmsProductImage(pmsProductImageList);
    }

    /**
     * 通过SPUID获取销售属性.
     * @param spuId SPU id
     * @return      销售属性列表
     */
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        //获取销售属性名
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr dto : pmsProductSaleAttrs) {
            //获取销售属性值
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setSaleAttrId(dto.getSaleAttrId());
            pmsProductSaleAttrValue.setProductId(spuId);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            dto.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    /**
     * 获取销售属性图片.
     * @param spuId spuid
     * @return
     */
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        return pmsProductImageMapper.select(pmsProductImage);
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
        return pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId, skuId);
    }

}
