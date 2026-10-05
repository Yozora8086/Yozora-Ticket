package com.lcy.yozoraticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private Long venueId;
    private String venueName;
    private String venueCity;
    private String venueAddress;
}
