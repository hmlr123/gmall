package com.hmlr123.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.OmsOrder;
import com.hmlr123.gmall.bean.OmsOrderItem;
import com.hmlr123.gmall.mq.ActiveMqUtil;
import com.hmlr123.gmall.order.mapper.OrderItemMapper;
import com.hmlr123.gmall.order.mapper.OrderMapper;
import com.hmlr123.gmall.order.utils.Constant;
import com.hmlr123.gmall.service.CartService;
import com.hmlr123.gmall.service.OrderService;
import com.hmlr123.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author liwei
 * @date 2019/9/14 16:43
 */
@Service(
        timeout = 1200000
)
public class OrderServiceImpl implements OrderService  {


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Reference
    private CartService cartService;

    @Autowired
    private ActiveMqUtil activeMqUtil;

    /**
     * 生成交易码.
     * 将交易码保存到数据库 过期时间60 * 15
     *
     * @param memberId
     * @return
     */
    @Override
    public String generalTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeCode = UUID.randomUUID().toString();
        String key = "user:" + memberId + ":tradeCode";
        jedis.setex(key, 60 * 15, tradeCode);
        jedis.close();
        return tradeCode;
    }

    /**
     * 检查交易码.
     *
     * @param memberId
     * @return
     */
    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();
        try {
            String key = "user:" + memberId + ":tradeCode";
            String code = jedis.get(key);
            if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(tradeCode) && tradeCode.equals(code)) {
                //清空交易码
                //使用lua脚本在发现key的同时将key删除，防止并发订单攻击
                Long eval = (Long) jedis.eval(Constant.LUA_SECRIPT, Collections.singletonList(key), Collections.singletonList(tradeCode));
                if (null != eval && 0 != eval) {
                    return "success";
                }
            }
            return "fail";

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 保存订单.
     *
     * @param order 订单
     */
    @Override
    public void saveOrder(OmsOrder order) {
        //保存订单和订单项 删除购物车相关数据
        List<OmsOrderItem> omsOrderItemList = order.getOmsOrderItemList();
        for (OmsOrderItem orderItem : omsOrderItemList) {
            orderItemMapper.insert(orderItem);
            //删除购物车相关数据
            cartService.deleteCart(orderItem.getProductId());
        }
        orderMapper.insert(order);
    }

    /**
     * 获取订单数据.
     *
     * @param outTradeNo    外部订单id
     * @return
     */
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        if (StringUtils.isNotBlank(outTradeNo)) {
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderSn(outTradeNo);
            return orderMapper.selectOne(omsOrder);
        }

        return null;
    }

    /**
     * 支付成功，修改订单状态
     * 修改订单状态.
     *
     * @param omsOrder
     */
    @Override
//    @Transactional
    public void updateOrder(OmsOrder omsOrder) {
        // 当前服务所作的事情
//        Example example = new Example(OmsOrder.class);
//        example.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());
//        OmsOrder order = new OmsOrder();
//        omsOrder.setStatus(1);
//        orderMapper.updateByExampleSelective(order, example);
//
//
//        //封装数据
//
//        //获取订单数据
//        OmsOrder omsOrderParam = new OmsOrder();
//        omsOrderParam.setOrderSn(omsOrder.getOrderSn());
//        OmsOrder omsOrderResponse = orderMapper.selectOne(omsOrderParam);
//
//        //获取订单项数据
//        OmsOrderItem omsOrderItem = new OmsOrderItem();
//        omsOrderItem.setOrderSn(omsOrder.getOrderSn());
//        List<OmsOrderItem> omsOrderItemList = orderItemMapper.select(omsOrderItem);
//
//        omsOrderResponse.setOmsOrderItemList(omsOrderItemList);
//
//        //放订单已支付队列给库存系统
//        TextMessage textMessage=new ActiveMQTextMessage();//字符串文本
//        try {
//            textMessage.setText(JSON.toJSONString(omsOrderResponse));
//            ActiveMqUtil.sendMessage(com.hmlr123.gmall.util.Constant.ORDER_PAY_QUEUE, textMessage, Session.SESSION_TRANSACTED);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
        Example e = new Example(OmsOrder.class);
        e.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());

        OmsOrder omsOrderUpdate = new OmsOrder();

        omsOrderUpdate.setStatus(1);

        // 发送一个订单已支付的队列，提供给库存消费
        Connection connection = null;
        Session session = null;
        try{
            connection = ActiveMqUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            Queue payhment_success_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(payhment_success_queue);
            TextMessage textMessage=new ActiveMQTextMessage();//字符串文本
            //MapMessage mapMessage = new ActiveMQMapMessage();// hash结构

            // 查询订单的对象，转化成json字符串，存入ORDER_PAY_QUEUE的消息队列
            OmsOrder omsOrderParam = new OmsOrder();
            omsOrderParam.setOrderSn(omsOrder.getOrderSn());
            OmsOrder omsOrderResponse = orderMapper.selectOne(omsOrderParam);

            OmsOrderItem omsOrderItemParam = new OmsOrderItem();
            omsOrderItemParam.setOrderSn(omsOrderParam.getOrderSn());
            List<OmsOrderItem> select = orderItemMapper.select(omsOrderItemParam);
            omsOrderResponse.setOmsOrderItemList(select);
            textMessage.setText(JSON.toJSONString(omsOrderResponse));

            orderMapper.updateByExampleSelective(omsOrderUpdate,e);
            producer.send(textMessage);
            session.commit();
        }catch (Exception ex){
            // 消息回滚
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                connection.close();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }

    }
}
