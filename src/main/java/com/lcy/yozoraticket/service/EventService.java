package com.lcy.yozoraticket.service;

import com.lcy.yozoraticket.vo.EventListVO;

import java.util.List;

public interface EventService {

    /**
     * 分页查询演出
     * @param page
     * @param pageSize
     * @return
     */
    List<EventListVO> selectEventList(Integer page, Integer pageSize);
}
