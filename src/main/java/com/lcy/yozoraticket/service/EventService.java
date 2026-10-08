package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.AddEventDTO;
import com.lcy.yozoraticket.dto.TicketTiersDTO;
import com.lcy.yozoraticket.vo.EventListVO;
import com.lcy.yozoraticket.vo.EventVO;

import java.util.List;

public interface EventService {

    /**
     * 分页查询演出
     * @param page
     * @param pageSize
     * @return
     */
    List<EventListVO> selectEventList(Integer page, Integer pageSize);

    /**
     * 新增演出
     * @param addEventDTO
     */
    void addEvent(AddEventDTO addEventDTO);

    /**
     * 增某一演出下的票档
     * @param ticketTiersDTOList
     */
    void addTicketLevel(List<TicketTiersDTO> ticketTiersDTOList);

    /**
     * 删除演出
     * @param eventId
     */
    void deleteEvent(Long eventId);

    /**
     * 查看演出详情
     * @param eventId
     * @return
     */
    EventVO getEventDetail(Long eventId);
}
