package com.lcy.yozoraticket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.EventDTO;
import com.lcy.yozoraticket.mapper.EventMapper;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.service.EventCacheService;
import com.lcy.yozoraticket.vo.EventSessionsVO;
import com.lcy.yozoraticket.vo.EventVO;
import com.lcy.yozoraticket.vo.TicketTiersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            //遍历每个演出下的演出场次
            for (EventSessionsVO eventSessionsVO : eventVO.getEventSessionsVOList()) {
                //判断当前场次的状态和下属的票档集合是否为空，有一满足则不参与遍历
                if (eventSessionsVO.getStatus() == 1 || (eventSessionsVO.getTicketTiersVOList() == null || eventSessionsVO.getTicketTiersVOList().isEmpty())) {
                    continue;
                }

                //创建map集合
                Map<String,String> map = new HashMap<>();
                //将演出场次票的票存量缓存到redis,当前创建一个空map。
                stringRedisTemplate.opsForHash().putAll(
                        RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionsVO.getEventSessionId(),
                         map
                );

                //遍历每个演出场次下的票档
                for (TicketTiersVO ticketTiersVO : eventSessionsVO.getTicketTiersVOList()) {
                    //判断当前票档的数据是否填入，如果未填入则为 -1
                    if (ticketTiersVO.getTicketPermit() == null){
                        ticketTiersVO.setTicketPermit(-1);
                    }
                    //判断当前票档是否存在，不存在则不参与缓存
                    if (ticketTiersVO.getTicketTiersId() == null) {
                        continue;
                    }
                    //将票等级,票价格和票剩余量放入map,field名称中包含票等级，避免出现同名field
                    map.put(
                            "ticketPermit" + ":" + ticketTiersVO.getTicketLevel().toString(),
                            ticketTiersVO.getTicketPermit().toString()
                    );
                    map.put(
                            "ticketPrice" + ":" + ticketTiersVO.getTicketLevel().toString(),
                            ticketTiersVO.getTicketPrice().toString()
                    );
                    //将演出场次票的票存量缓存到redis
                    stringRedisTemplate.opsForHash().putAll(
                            RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionsVO.getEventSessionId(),
                            map
                    );
                    //设置数据过期时间
                    stringRedisTemplate.expire(
                            RedisConstant.WARMUP_TICKET_PERMIT_KEY + eventSessionsVO.getEventSessionId(),
                            7,
                            TimeUnit.DAYS
                    );
                }
            }

        }
    }
}
