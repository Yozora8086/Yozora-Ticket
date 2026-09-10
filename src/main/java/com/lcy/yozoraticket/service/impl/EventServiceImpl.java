package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.mapper.EventMapper;
import com.lcy.yozoraticket.service.EventService;
import com.lcy.yozoraticket.vo.EventListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

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
}
