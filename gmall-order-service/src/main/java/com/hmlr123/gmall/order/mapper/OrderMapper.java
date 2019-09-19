package com.hmlr123.gmall.order.mapper;

import com.hmlr123.gmall.bean.OmsOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author liwei
 * @date 2019/9/14 21:05
 */
public interface OrderMapper extends Mapper<OmsOrder> {

    /**
     * 修改订单状态
     *
     * @param orderSn   外部订单号
     */
    void updateStatusByOrderSn(@Param("orderSn") String orderSn, @Param("status") String status);
}
