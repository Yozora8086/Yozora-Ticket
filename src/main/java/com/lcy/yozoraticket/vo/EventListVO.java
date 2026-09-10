package com.lcy.yozoraticket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListVO {
    //演出id
    private Long eventId;
    //演出名
    private String eventName;
    //演出海报
    private String eventPoster;
    //演出类型
    private String eventGenre;
    //演出状态(0 = 未上架, 1 = 正常展示/售卖中, 2 = 已下架, 3 = 已结束, 4 = 已取消)
    private Byte eventStatus;
}
