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
}
