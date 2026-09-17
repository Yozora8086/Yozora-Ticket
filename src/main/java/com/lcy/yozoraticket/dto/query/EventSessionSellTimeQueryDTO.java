package com.lcy.yozoraticket.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSessionSellTimeQueryDTO {
    //当前场次演出票开售时间
    private LocalDateTime sellTime;
    //当前演出场次状态 0 = 正常 1 = 已取消
    private Byte status;
}
