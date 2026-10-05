package com.lcy.yozoraticket.dto;

import com.lcy.yozoraticket.entity.EventSessions;
import com.lcy.yozoraticket.entity.Venue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEventDTO {
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
    //演出场次集合
    private List<EventSessionsDTO> eventSessionsDTOList;
    //演出场地对象
    private VenueDTO venueDTO;

}
