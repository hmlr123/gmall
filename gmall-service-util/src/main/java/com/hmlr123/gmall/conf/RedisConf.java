package com.hmlr123.gmall.conf;

import com.hmlr123.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置类，Spring容器初始化之后redis生成单实例.
 * redis链接池配置到spring容器中
 *
 * @author liwei
 * @date 2019/9/5 14:58
 */
@Configuration
public class RedisConf {
    //读取配置文件中的redis的ip地址 默认不配置disabled
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * 初始化redis 不同服务初始化的redis不同.
     * 单例模式
     * @return  RedisUtil
     */
    @Bean
    public RedisUtil getRedisUtil() {
        if ("disabled".equals(host)) {
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initPool(host, port, database);
        return redisUtil;
    }
}
