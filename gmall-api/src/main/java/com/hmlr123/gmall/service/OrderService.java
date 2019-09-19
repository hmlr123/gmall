package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.OmsOrder;

/**
 * @author liwei
 * @date 2019/9/14 16:42
 */
public interface OrderService {

    /**
     * 生成交易码.
     *
     * @param memberId
     * @return
     */
    String generalTradeCode(String memberId);

    /**
     * 检查交易码.
     *
     * @param memberId
     * @return
     */
    String checkTradeCode(String memberId, String tradeCode);

    /**
     * 保存订单.
     *
     * @param order 订单
     */
    void saveOrder(OmsOrder order);

    /**
     * 获取订单数据.
     *
     * @param outTradeNo    外部订单id
     * @return
     */
    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    /**
     * 修改订单状态.
     *
     * @param omsOrder
     */
    void updateOrder(OmsOrder omsOrder);
}
