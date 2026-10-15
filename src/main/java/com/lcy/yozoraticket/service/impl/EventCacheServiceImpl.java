package com.lcy.yozoraticket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.EventDTO;
import com.lcy.yozoraticket.mapper.EventMapper;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.service.EventCacheService;
import com.lcy.yozoraticket.vo.EventVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EventCacheServiceImpl implements EventCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final EventMapper eventMapper;
    private final EventSessionsMapper eventSessionsMapper;
    private final ObjectMapper objectMapper;

    /**
     * 演出预热
     */
    @Override
    public void warmupSellingEvents() {
        //查询演出场次id(按照状态查询 status = 0)
        List<Long> ids = eventSessionsMapper.selectEventSessionIdByStatus();
        //根据演出id集合查询演出详情
        List<EventVO> eventVOList = eventMapper.selectEventDetailList(ids);

        //遍历演出详情集合
        for (EventVO eventVO : eventVOList) {
            String eventDetail;
            try {
                //对象转json
                eventDetail = objectMapper.writeValueAsString(eventVO);
            } catch (JsonProcessingException e){
                throw new IllegalStateException(e);
            }
            //缓存到redis
            stringRedisTemplate.opsForValue().set(
                    RedisConstant.WARMUP_EVENT_KEY + eventVO.getEventId(),
                    eventDetail,
                    7,
                    TimeUnit.DAYS
            );
        }

    }
}
