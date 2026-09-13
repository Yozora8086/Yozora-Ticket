package com.lcy.yozoraticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    //支付记录id
    private Long paymentId;
    //订单id
    private Long orderId;
    //系统生成的支付流水号
    private String paymentNo;
    //支付金额
    private Integer amount;
    //支付状态：0待支付，1支付成功，2支付失败，3已关闭
    private Byte status;
    //支付成功时间
    private LocalDateTime paySuccessTime;
    //支付记录创建时间
    private LocalDateTime createPayTime;

}
