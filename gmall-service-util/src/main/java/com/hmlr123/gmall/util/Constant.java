package com.hmlr123.gmall.util;

/**
 * 公共常量.
 *
 * @author liwei
 * @date 2019/9/17 10:01
 */
public class Constant {

    //消息队列监听名称
    public final static String JMS_QUEUE_LISTENER = "jmsQueueListener";

    //提交订单的延迟检查(支付服务)
    public final static String PAYMENT_CHECK_QUEUE = "PAYMENT_CHECK_QUEUE";

    //支付完成(支付服务)
    public final static String PAYMENT_SUCCESS_QUEUE = "PAYMENT_SUCCESS_QUEUE";

    //订单已支付(订单服务)
    public final static String ORDER_PAY_QUEUE = "ORDER_PAY_QUEUE";

    //库存锁定(库存系统)
    public final static String SKU_DEDUCT_QUEUE = "SKU_DEDUCT_QUEUE";

    //订单已出库(订单服务)
    public final static String ORDER_SUCCESS_QUEUE = "SKU_DEDUCT_QUEUE";
}
