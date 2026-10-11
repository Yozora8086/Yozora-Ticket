package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.dto.EventSessionsDTO;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.service.EventSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventSessionServiceImpl implements EventSessionService {

    private final EventSessionsMapper eventSessionsMapper;

    /**
     * 修改演出场次信息
     * @param eventSessionsDTO
     */
    @Override
    public void updateEventSession(EventSessionsDTO eventSessionsDTO) {
       eventSessionsMapper.updateEventSession(eventSessionsDTO);
    }
}
