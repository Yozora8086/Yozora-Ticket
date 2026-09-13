package com.lcy.yozoraticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    //电子票id
    private Long ticketId;
    private Long orderItemId;
}
