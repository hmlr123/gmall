package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.PaymentInfo;

import java.util.Map;

/**
 * @author liwei
 * @date 2019/9/15 20:39
 */
public interface PaymentService {

    /**
     * 将订单数据保存到数据库.
     *
     * @param paymentInfo   订单数据
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 修改订单状态.
     *
     * @param paymentInfo   订单数据
     */
    void updatePayment(PaymentInfo paymentInfo);

    /**
     * 发送延迟消息队列  用于检查支付状态，化被动为主动.
     *
     * @param outTradeNo    外部交易码
     * @param num           失败重新请求次数
     */
    void sendDelayPaymentResultCheckQueue(String outTradeNo, Integer count);

    /**
     * 请求alipay 获取支付状态
     *
     * @param outTradeNo    外部交易码
     * @return
     */
    Map<String, Object> checkAlipayPayment(String outTradeNo);
}
