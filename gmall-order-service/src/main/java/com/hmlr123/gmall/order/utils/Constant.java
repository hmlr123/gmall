package com.hmlr123.gmall.order.utils;

/**
 * @author liwei
 * @date 2019/9/14 17:31
 */
public class Constant {

    public final static String LUA_SECRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
}
