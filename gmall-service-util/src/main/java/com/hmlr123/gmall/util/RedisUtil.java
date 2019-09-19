package com.hmlr123.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis工具类.
 *
 * @author liwei
 * @date 2019/9/5 14:59
 */
public class RedisUtil {

    /**
     * redis连接池常量.
     */
    private JedisPool jedisPool;

    /**
     * 初始化redis 不同服务的初始化不一样.
     *
     * @param host      IP地址
     * @param port      端口
     * @param database  数据库0-16
     */
    public void initPool(String host, int port, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(20);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10*1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, host, port, 20*1000);
    }

    /**
     * 获取redis连接.
     *
     * @return  Jedis
     */
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
