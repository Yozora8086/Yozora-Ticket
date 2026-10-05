package com.lcy.yozoraticket.mapper;

import com.lcy.yozoraticket.dto.VenueDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VenueMapper {

    void addVenue(VenueDTO venueDTO);
}
