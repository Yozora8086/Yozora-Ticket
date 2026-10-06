package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.dto.EventSessionsDTO;
import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.mapper.EventMapper;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import com.lcy.yozoraticket.mapper.VenueMapper;
import com.lcy.yozoraticket.service.EventService;
import com.lcy.yozoraticket.vo.EventListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final VenueMapper venueMapper;
    private final EventSessionsMapper eventSessionsMapper;
    private final TicketTiersMapper ticketTiersMapper;

    /**
     * 分页查询演出
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<EventListVO> selectEventList(Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<EventListVO> eventList = eventMapper.selectEventList(offset,pageSize);
        return eventList;
    }

    /**
     * 新增演出
     * @param addEventDTO
     */
    @Transactional
    @Override
    public void addEvent(AddEventDTO addEventDTO) {
        //新增演出并回填id
        eventMapper.addEvent(addEventDTO);

        //新增演出地址并回填id
        venueMapper.addVenue(addEventDTO.getVenueDTO());

        //遍历演出场次,将场次对应的演出和场地存入
        for (EventSessionsDTO eventSessionsDTO : addEventDTO.getEventSessionsDTOList()) {
            eventSessionsDTO.setEventId(addEventDTO.getEventId());
            eventSessionsDTO.setVenueId(addEventDTO.getVenueDTO().getVenueId());
        }

        //数据库场次新增
        eventSessionsMapper.addEventSessionList(addEventDTO.getEventSessionsDTOList());

    }


    /**
     * 增某一演出下的票档
     * @param ticketTiersDTOList
     */
    @Override
    public void addTicketLevel(List<TicketTiersDTO> ticketTiersDTOList) {
        ticketTiersMapper.addTicketLevel(ticketTiersDTOList);
    }
}
