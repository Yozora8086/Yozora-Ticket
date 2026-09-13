package com.lcy.yozoraticket.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemQueryDTO {

    //下单时的演出名称
    private String eventName;
    //下单时的场次时间
    private LocalDateTime startTime;
    //下单时的场馆名称
    private String venueName;
}
