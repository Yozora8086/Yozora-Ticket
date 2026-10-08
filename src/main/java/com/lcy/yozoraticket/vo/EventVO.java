package com.lcy.yozoraticket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventVO {
    //演出id
    private Long eventId;
    //演出名
    private String eventName;
    //演出介绍
    private String eventIntroduction;
    //演出海报
    private String eventPoster;
    //类型
    private String eventGenre;
    //演出状态
    private Byte eventStatus;
    //演出场次集合
    private List<EventSessionsVO> eventSessionsVOList;

}
