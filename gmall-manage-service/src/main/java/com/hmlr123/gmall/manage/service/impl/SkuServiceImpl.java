package com.hmlr123.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.PmsSkuAttrValue;
import com.hmlr123.gmall.bean.PmsSkuImage;
import com.hmlr123.gmall.bean.PmsSkuInfo;
import com.hmlr123.gmall.bean.PmsSkuSaleAttrValue;
import com.hmlr123.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.hmlr123.gmall.manage.mapper.PmsSkuImageMapper;
import com.hmlr123.gmall.manage.mapper.PmsSkuInfoMapper;
import com.hmlr123.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.hmlr123.gmall.service.SkuService;
import com.hmlr123.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName: SkuServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/24 17:37
 * @Version: 1.0
 */
@Service(
        timeout = 1200000
)
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 保存SKU数据.
     *
     * @param pmsSkuInfo    SKU数据
     */
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //保存SKU数据
        pmsSkuInfoMapper.insert(pmsSkuInfo);

        //保存平台sku销售属性和销售属性关联表
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue dto : skuAttrValueList) {
            dto.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insert(dto);
        }
        //保存平台sku销售属性值
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        //保存图片数据
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }

    }

    /**
     * 从数据库获取数据.
     *
     * @param skuId sku数据
     * @return
     */
    @Override
    public PmsSkuInfo getBySkuIdFromDb(String skuId) {
        //Sku 商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    /**
     * 根据三级分类id获取所有sku数据。
     *
     * @param catalog3Id    三类分级id
     * @return              PmsSkuInfo数据集合
     */
    @Override
    public List<PmsSkuInfo> getAllSkuInfo(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            String skuId = pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfoList;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        if (0 == skuInfo.getPrice().compareTo(price)) {
            return true;
        }
        return false;
    }

    /**
     * 获取sku数据.
     *
     * @param skuId sku数据
     * @return      sku实体数据
     */
    @Override
    public PmsSkuInfo getBySkuId(String skuId, String ip) {
        //获取连接
        Jedis jedis = redisUtil.getJedis();
        //Key
        String skuKey = "sku:" + skuId + ":info";
        //从缓存中获取数据
        String pmsSkuInfoStr = jedis.get(skuKey);
        PmsSkuInfo pmsSkuInfo = null;
        if (StringUtils.isNotBlank(pmsSkuInfoStr)) {
            pmsSkuInfo = JSON.parseObject(pmsSkuInfoStr, PmsSkuInfo.class);
            System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + "  缓存中存在数据");
        } else {
            //设置分布式锁 防止redis击穿

            //防止请求线程还没结束，当前锁失效 加个标识位
            String token = UUID.randomUUID().toString();
            //锁有 key value 过期时间 加锁策略
            String ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "ex", 10);
            if (StringUtils.isNotBlank(ok) && "OK".equals(ok)) {
                System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + "sku:" + skuId + ":lock" + " 取得对象锁");
                //设置成功，有权在10s内访问数据库
                pmsSkuInfo = this.getBySkuIdFromDb(skuId);
                if (null != pmsSkuInfo) {
                    System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 数据写入缓存");
                    jedis.set("sku:" + pmsSkuInfo.getId() + ":info", JSON.toJSONString(pmsSkuInfo));
                } else {
                    System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 数据库不存在该记录");
                    //数据库中不存在该sku 防止数据库穿透 过期时间3分钟
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                }
                System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 释放锁");
                //方案一:增加锁标识 在删除的时候判断判断是我们标识的锁.
//                //释放锁 判断是否是之前标识的那个，如果是就可以删除
//                String localToken = jedis.get("sku:" + skuId + ":lock");
//                if (StringUtils.isNotBlank(localToken) && localToken.equals(token)) {
//                    //说明锁还没有过期
//                    jedis.del("sku:" + skuId + ":lock");
//                }

                //方案二:方案一存在在判断的时候锁失效,导致删除其他线程的锁.
                //A:使用lua脚本,在获取我们标识位的同时删除我们的锁,(不经过我们java,在redis中删除)
                // 思考,跨技术的交互都会存在这种问题.比如java的原子性操作CAS就是通过底层实现的.
                List<String> keys  = new ArrayList();
                keys.add("sku:" + skuId + ":lock");
                List<String> tokens = new ArrayList<>();
                tokens.add(token);
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, keys, tokens);
            } else {
                System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 开始自旋");
                //设置失败 自旋，睡眠几秒后重新访问
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //注意必须用return 否则重新开辟了新的线程
                return this.getBySkuId(skuId, ip);
            }

        }
        return pmsSkuInfo;
    }

    /**
     * 获取其他sku数据.
     *
     * @param productId     一类产品id
     * @return              sku集合
     */
    @Override
    public List<PmsSkuInfo> getPmsSkuSaleAttrValueByProductId(String productId) {
        return pmsSkuInfoMapper.getPmsSkuSaleAttrValueByProductId(productId);
    }
}
