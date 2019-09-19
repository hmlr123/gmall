package com.hmlr123.gmall.order.mq;

import com.hmlr123.gmall.bean.OmsOrder;
import com.hmlr123.gmall.service.OrderService;
import com.hmlr123.gmall.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * 订单监听消息队列
 *
 * @author liwei
 * @date 2019/9/16 20:43
 */
@Component
public class OrderServiceMqListener {

    @Autowired
    OrderService orderService;

    /**
     * 消费者.
     * 消费 PAYMENT_SUCCESS_QUEUE
     * 源自 jmsQueueListener监听工厂
     *
     * 1. 接受支付已成功的消息队列，通知订单服务修改订单状态
     * 2. 通知库存系统锁定库存，拆单
     *
     * @param mapMessage
     */
    @JmsListener(destination = Constant.PAYMENT_SUCCESS_QUEUE, containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {
        String trade_no = mapMessage.getString("out_trade_no");

        //更新订单状态
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(trade_no);
        orderService.updateOrder(omsOrder);
    }
}
