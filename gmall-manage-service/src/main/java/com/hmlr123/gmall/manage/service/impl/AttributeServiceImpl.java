package com.hmlr123.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.PmsBaseAttrInfo;
import com.hmlr123.gmall.bean.PmsBaseAttrValue;
import com.hmlr123.gmall.bean.PmsBaseSaleAttr;
import com.hmlr123.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.hmlr123.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.hmlr123.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.hmlr123.gmall.service.AttributeService;
import com.hmlr123.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: AttributeServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 0:34
 * @Version: 1.0
 */
@Service(
        timeout = 1200000
)
public class AttributeServiceImpl implements AttributeService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 根据商品id获取系列商品的平台属性.
     * @param catalog3Id    商品分类id
     * @return              商品属性列表
     */
    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        //获取平台属性
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);

        //封装平台属性值
        for (PmsBaseAttrInfo dto : pmsBaseAttrInfos) {
            //通过属性id获取属性值
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(dto.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            dto.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }


    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        String id = pmsBaseAttrInfo.getId();
        if (StringUtils.isBlank(id)) {
            //保存操作
            /**
             * insertSelective null不写入数据库
             * insert          全部写入数据库
             */
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            }
            pmsBaseAttrValueMapper.batchInsertPmsBaseAttrValue(pmsBaseAttrInfo.getAttrValueList());
        } else {
            //修改操作
            //修改属性名
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id",pmsBaseAttrInfo.getId());
            //目标值 修改规则
            pmsBaseAttrInfoMapper.updateByExample(pmsBaseAttrInfo,example);

            //删除原有属性值
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueMapper.delete(pmsBaseAttrValue);

            //新增属性值
            for (PmsBaseAttrValue baseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                baseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            }
            //批量保存
            if (pmsBaseAttrInfo.getAttrValueList().size() > 0) {
                pmsBaseAttrValueMapper.batchInsertPmsBaseAttrValue(pmsBaseAttrInfo.getAttrValueList());
            }
        }

    }

    /**
     * 根据属性id获取属性值
     * @param attrId    属性id
     * @return          属性值实体集合
     */
    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
    }

    /**
     * 获取平台销售属性字典表数据
     * @return
     */
    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    /**
     * 获取平台属性集合
     *
     * @param valueIdSet    平台属性值id
     * @return              平台属性集合
     */
    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {
        Jedis jedis = redisUtil.getJedis();

        //集合对象分割成字符串
        if (null != valueIdSet && valueIdSet.size() > 0) {
            String join = StringUtils.join(valueIdSet, ",");
            return pmsBaseAttrInfoMapper.getAttrValueListByValueId(join);
        } else {
            return null;
        }
    }

    /**
     * 获取平台属性全部数据.
     *
     * @return 平台属性集合
     */
    @Override
    public List<PmsBaseAttrInfo> getAllAttrValueList() {
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectAll();
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfoList) {
            String id = pmsBaseAttrInfo.getId();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(id);
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfoList;
    }


}
