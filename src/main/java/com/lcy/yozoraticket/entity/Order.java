package com.lcy.yozoraticket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    //订单id
    private Long orderId;
    //演出id
//    private Long eventId;
    //演出场次id
    private Long eventSessionId;
    //订单号
    private String orderNo;
    //用户id
    private Long userId;
    //票级别
    private Byte ticketLevel;
    //总价(合计金额)
    private Integer total;
    //购买总张数
    private Byte ticketTotal;
    //当前状态(0 = 待支付, 1 = 已支付, 2 = 已取消, 3 = 已超时, 4 = 已退款)
    private Byte status;
    //订单关闭时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payCloseTime;
}
