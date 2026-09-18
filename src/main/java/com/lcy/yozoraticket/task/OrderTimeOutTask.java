package com.lcy.yozoraticket.task;

import com.lcy.yozoraticket.service.OrderTimeOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderTimeOutTask {

    private final OrderTimeOutService orderTimeOutService;


    /**
     * 定时方法，来判断未支付订单是否超时，超时则退回锁定的库存
     */
    @Scheduled(fixedDelay = 60000)
    public void closeOrder(){

        //处理订单超时和超时订单锁定的票回到票池
        orderTimeOutService.closeOrder();

    }

}
