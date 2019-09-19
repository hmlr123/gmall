package com.hmlr123.gmall.payment.mq;

import com.hmlr123.gmall.bean.OmsOrder;
import com.hmlr123.gmall.bean.PaymentInfo;
import com.hmlr123.gmall.service.PaymentService;
import com.hmlr123.gmall.util.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

/**
 * Payment队列监听类
 *
 * @author liwei
 * @date 2019/9/17 16:50
 */
@Component
public class PaymentServiceMqListener {

    @Autowired
    PaymentService paymentService;


    /**
     * 接收 PAYMENT_CHECK_QUEUE 延迟队列，检查支付情况
     * 成功，修改订单状态
     * 失败，继续发送延迟队列请求aliPay获取支付情况，fail_count 失败重传次数，还可以加上失败重传时间间隔.
     * 重传次数用完，支付失败，业务回滚
     *
     * @param mapMessage
     * @throws JMSException
     */
    @JmsListener(destination = Constant.PAYMENT_CHECK_QUEUE, containerFactory = Constant.JMS_QUEUE_LISTENER)
    public void consumePaymentResult(MapMessage mapMessage) throws JMSException {
        //外部交易码
        String outTradeNo = mapMessage.getString("out_trade_no");
        //失败重传次数
        Integer count = mapMessage.getInt("fail_count");
        //检查支付情况 请求alipay
        Map<String, Object> alipayResult = paymentService.checkAlipayPayment(outTradeNo);

        /**
         * 判断支付状态，决定要做什么.
         * 1. 执行下一次延迟任务
         * 2. 更新数据和后续任务
         */

        //当前支付宝交付状态
        String tradeStatus = (String) alipayResult.get("trade_status");

        if (null != alipayResult) {
            if (StringUtils.isNotBlank(tradeStatus) &&  "TRADE_SUCCESS".equals(tradeStatus)) {
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(outTradeNo);
                //支付成功 修改支付状态
                paymentService.updatePayment(paymentInfo);
                return;
            }
        }

        //支付失败
        if (count > 0) {
            //减少死循环次数
            count--;
            //支付失败 发送延迟队列 如果用户没有支付，会陷入死循环
            paymentService.sendDelayPaymentResultCheckQueue(outTradeNo, count);
        } else {
            //结束检查，支付失败 回滚记录
        }

    }
}
