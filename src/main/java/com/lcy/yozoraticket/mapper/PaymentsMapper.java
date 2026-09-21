package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.entity.Payments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentsMapper {

    /**
     * 数据库插入支付记录
     * @param payments
     */
    void insertPayment(Payments payments);


    /**
     * 数据库修改支付记录状态（取消支付）
     * @param orderId
     * @param paymentStatus
     */
    Integer updateStatus(Long orderId, Byte paymentStatus);

    /**
     * 数据库修改支付记录状态 (成功支付)
     * @param orderId
     * @param paymentStatus
     */
    Integer updateStatusCommit(Long orderId, Byte paymentStatus,Byte nowStatus);
}
