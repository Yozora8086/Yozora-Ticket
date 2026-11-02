package com.lcy.yozoraticket.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcy.yozoraticket.constant.RedisConstant;
import com.lcy.yozoraticket.dto.EventOrderDTO;
import com.lcy.yozoraticket.dto.TicketBuyDTO;
import com.lcy.yozoraticket.dto.query.OrderItemQueryDTO;
import com.lcy.yozoraticket.entity.Order;
import com.lcy.yozoraticket.entity.OrderItem;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.mapper.EventSessionsMapper;
import com.lcy.yozoraticket.mapper.OrderItemMapper;
import com.lcy.yozoraticket.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final EventSessionsMapper eventSessionsMapper;
    private final OrderItemMapper orderItemMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 生成订单明细
     * @param ticketBuyDTO 购票参数对象
     * @param eventOrderDTO 购买的票所包含的演出信息
     * @return
     */
    @Override
    public OrderItem orderItem(TicketBuyDTO ticketBuyDTO, EventOrderDTO eventOrderDTO) {

        //创建订单明细对象
        OrderItem orderItem = OrderItem.builder()
                .orderId(eventOrderDTO.getOrderId())
                .orderItemId(IdUtil.getSnowflakeNextId())
                .ticketLevel(ticketBuyDTO.getTicketLevel())
                .count(ticketBuyDTO.getCount())
                .price(eventOrderDTO.getEventSessionNoListDTO().getTicketTiersVO().getTicketPrice())
                .eventName(eventOrderDTO.getEventName())
                .sessionTime(eventOrderDTO.getEventSessionNoListDTO().getStartTime())
                .venueName(eventOrderDTO.getEventSessionNoListDTO().getVenueVO().getVenueName())
                .build();

        //将订单详情对象序列化成json
        String orderItemJson;
        try {
            orderItemJson = objectMapper.writeValueAsString(orderItem);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_SERIALIZATION_ERROR);
        }


        //将订单详情缓存到redis
        stringRedisTemplate.opsForValue().set(
                RedisConstant.TICKET_ORDER_ITEM_KEY + orderItem.getOrderId().toString(),
                orderItemJson,
                7,
                TimeUnit.DAYS
        );

        return orderItem;
    }
}
