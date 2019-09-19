/*
package com.hmlr123.gmall.util;

import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.PmsBaseAttrInfo;
import com.hmlr123.gmall.service.AttributeService;
import com.hmlr123.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

*/
/**
 * 缓存工具类.
 *
 * @author liwei
 * @date 2019/9/9 12:13
 *//*

public class CacheUtils {

    */
/**
     * redis连接池常量.
     *//*

    private static JedisPool jedisPool;

    */
/**
     * 初始化redis 不同服务的初始化不一样.
     *
     * @param host      IP地址
     * @param port      端口
     * @param database  数据库0-16
     *//*

    public static void initPool(String host, int port, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(20);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10*1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, host, port, 20*1000);
    }

    private static synchronized void poolInit() {

    }

    */
/**
     * 获取redis连接.
     *
     * @return  Jedis
     *//*

    public static Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public static <T> T  hget(AttributeService attributeService) {
        Jedis jedis = getJedis();
        String hget = jedis.hget(RedisUtil.class.getName(), PmsBaseAttrInfo.class.getSimpleName());
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(hget)) {
            pmsBaseAttrInfoList = JSON.parseArray(hget, PmsBaseAttrInfo.class);
        } else {
            //设置分布式锁
            //分布式锁value 标识 防止删除别的key
            String token = UUID.randomUUID().toString();
            //加锁
            String ok = jedis.set("attr:" + PmsBaseAttrInfo.class
                    .getName() + ":lock", token, "nx", "ex", 10);
            if (StringUtils.isNotBlank(ok) && "OK".equals(ok)) {
                //设置锁成功 写入数据
                pmsBaseAttrInfoList = attributeService.getAllAttrValueList();
                if (null == pmsBaseAttrInfoList) {
                    //数据库没有数据， 防止穿透 设置数据
                    jedis.hset(RedisUtil.class.getName(), PmsBaseAttrInfo.class.getSimpleName(), "")
                } else {
                    jedis.hset(RedisUtil.class.getName(), PmsBaseAttrInfo.class.getSimpleName(), )
                }


            }


        }

    }





}
*/
