package com.lcy.yozoraticket.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSuccessMessage {
    //订单id
    private Long orderId;
    //购买数量
    private Byte count;
    //演出场次id
    private Long eventSessionId;
    //票等级
    private Byte ticketLevel;
    //修改的目标状态
    private Byte status;
}
