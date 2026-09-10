package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketBuyDTO {

    //演出场次id
    private Long eventSessionId;
    //票等级
    private Byte ticketLevel;
    //购票数量
    private Byte count;

}
