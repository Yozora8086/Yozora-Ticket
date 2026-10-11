package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.dto.EventSessionsDTO;

public interface EventSessionService {

    /**
     * 修改演出场次信息
     * @param eventSessionsDTO
     */
    void updateEventSession(EventSessionsDTO eventSessionsDTO);
}
