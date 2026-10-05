package com.lcy.yozoraticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueDTO {
    //演出场地id
    private Long venueId;
    //演出场地名
    private String venueName;
    //演出城市
    private String venueCity;
    //演出地址
    private String venueAddress;
}
