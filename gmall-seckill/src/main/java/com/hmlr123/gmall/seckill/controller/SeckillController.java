package com.hmlr123.gmall.seckill.controller;

import com.hmlr123.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @author liwei
 * @date 2019/9/19 16:26
 */
@Controller
public class SeckillController {

    @Autowired
    RedisUtil redisUtil;

    /**
     * 使用Redissien框架
     */
    @Autowired
    RedissonClient redissonClient;


    /**
     * 一起请求的一批人中只有一个可以获得.
     * 拼手气
     *
     * @return
     */
    @RequestMapping("kill")
    @ResponseBody
    public Integer kill() {

        Jedis jedis = redisUtil.getJedis();
        //监视
        jedis.watch("106");
        Integer stock = Integer.valueOf(jedis.get("106"));

        if (stock > 0) {
            //开启事务
            Transaction multi = jedis.multi();
            //减少数量
            multi.incrBy("106", -1);
            //执行事务
            List<Object> exec = multi.exec();
            if (null != exec && exec.size() > 0) {
                System.out.println("抢购成功," + "剩余库存：" + stock + "当前抢购人数：" + (1000 - stock));
            } else {
                System.out.println("抢购失败" + "剩余库存：" + stock + "当前抢购人数：" + (1000 - stock));
            }
        }
        jedis.close();
        return 1;
    }

    /**
     * 先到先得
     *
     * @return
     */
    @RequestMapping("secKill")
    @ResponseBody
    public Integer secKill() {
        //获取信号量
        RSemaphore semaphore = redissonClient.getSemaphore("106");
        //尝试减少操作
        boolean b = semaphore.tryAcquire();
        //获取redis连接
        Jedis jedis = redisUtil.getJedis();
        //查询剩余数量
        Integer stock = Integer.valueOf(jedis.get("106"));
        if (b) {
            System.out.println("当前库存剩余数量" + stock + ",某用户抢购成功，当前抢购人数：" + (1000 - stock));
            // 用消息队列发出订单消息
            System.out.println("发出订单的消息队列，由订单系统对当前抢购生成订单");
        } else {
            System.out.println("当前库存剩余数量" + stock + ",某用户抢购失败");
        }
        jedis.close();
        return 1;
    }
}
