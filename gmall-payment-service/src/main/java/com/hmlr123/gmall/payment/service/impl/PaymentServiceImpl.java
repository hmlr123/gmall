package com.hmlr123.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.hmlr123.gmall.bean.PaymentInfo;
import com.hmlr123.gmall.mq.ActiveMqUtil;
import com.hmlr123.gmall.payment.mapper.PaymentMapper;
import com.hmlr123.gmall.service.PaymentService;
import com.hmlr123.gmall.util.Constant;
import com.hmlr123.gmall.util.HttpclientUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author liwei
 * @date 2019/9/15 20:40
 */
@Service(
        timeout = 120000
)
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentMapper paymentMapper;

  /**
     * 将订单数据保存到数据库.
     *
     * @param paymentInfo   订单数据
     */
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentMapper.insertSelective(paymentInfo);
    }

    /**
     * 修改订单状态.
     *
     * @param paymentInfo   订单数据
     */
    @Override
//    @Transactional
    public void updatePayment(PaymentInfo paymentInfo) {

        //幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoResult = paymentMapper.selectOne(paymentInfoParam);
        String paymentStatus = paymentInfoResult.getPaymentStatus();
        if (StringUtils.isNotBlank(paymentStatus) && "已支付".equals(paymentStatus)) {
            return;
        }

        String orderSn = paymentInfo.getOrderSn();
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", orderSn);
        paymentMapper.updateByExampleSelective(paymentInfo, example);


        try {
            MapMessage message = new ActiveMQMapMessage();
            message.setString("out_trade_no", paymentInfo.getOrderSn());
            ActiveMqUtil.sendMessage(Constant.PAYMENT_SUCCESS_QUEUE, message, Session.SESSION_TRANSACTED);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送延迟消息队列  用于检查支付状态，化被动为主动.
     * 避免支付宝没有给我们返回支付情况
     *
     * @param outTradeNo
     */
    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo, Integer count) {
        MapMessage message = new ActiveMQMapMessage();

        try {
            // 外部交易码 用于识别我们没笔订单
            message.setString("out_trade_no", outTradeNo);
            // 错误请求次数 可以加上错误请求时间间隔.
            message.setInt("fail_count", count);
            // 消息添加延时时间 使用延迟队列需要开启延迟队列.
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 30);
            ActiveMqUtil.sendMessage(Constant.PAYMENT_CHECK_QUEUE, message, Session.SESSION_TRANSACTED);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求aliPay 获取支付状态
     *
     * @param outTradeNo    外部交易码
     * @return
     */
    @Override
    public Map<String, Object> checkAlipayPayment(String outTradeNo) {
        // 模拟请求aliPay
        Map<String, String> aliPayParam = new HashMap<>();
        aliPayParam.put("out_trade_no", outTradeNo);        //商户订单号
        String url = "http://payment.gmall.hmlr123.com/alipay/checkStatus";
        String strMap = HttpclientUtil.doPost(url, aliPayParam);
        Map<String, Object> map = JSONObject.parseObject(strMap, Map.class);
        return map;
    }
}
