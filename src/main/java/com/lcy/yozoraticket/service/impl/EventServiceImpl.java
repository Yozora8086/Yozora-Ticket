package com.lcy.yozoraticket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.dto.EventDTO;
import com.lcy.yozoraticket.dto.EventSessionsDTO;
import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.mapper.EventMapper;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.mapper.TicketTiersMapper;
import com.lcy.yozoraticket.mapper.VenueMapper;
import com.lcy.yozoraticket.service.EventService;
import com.lcy.yozoraticket.vo.EventListVO;
import com.lcy.yozoraticket.vo.EventVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

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


    /**
     * 删除演出
     * @param eventId
     */
    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        //获取当前演出下的所有演出场次的id
        List<Long> eventSessionList = eventSessionsMapper.getIdByEventIds(eventId);

        //删除演出下所属的演出场次下属的所有票档
        ticketTiersMapper.deleteByEventSessionId(eventSessionList);

        //删除演出下所属的演出场次
        eventSessionsMapper.deleteByEventId(eventId);

        //删除演出
        eventMapper.deleteEventById(eventId);
    }

    /**
     * 查看演出详情
     * @param eventId
     * @return
     */
    @Override
    public EventVO getEventDetail(Long eventId) {
        //获取redis中缓存的演出信息
        String eventDetail = stringRedisTemplate.opsForValue().get(RedisConstant.WARMUP_EVENT_KEY + eventId);
        EventVO eventVO;
        try {
            //将json转成EventVO对象
            eventVO = objectMapper.readValue(eventDetail, EventVO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

//        EventVO eventVO = eventMapper.selectEventDetail(eventId);
        return eventVO;
    }

    /**
     * 修改演出信息
     * @param eventDTO
     */
    @Override
    public void updateEvent(EventDTO eventDTO) {
        eventMapper.updateEvent(eventDTO);
    }
}
