package com.lcy.yozoraticket.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyTicketCommand {

    //票等级
    private Byte ticketLevel;
    //购票数量
    private Byte count;
    //票价
    private Integer price;
}
