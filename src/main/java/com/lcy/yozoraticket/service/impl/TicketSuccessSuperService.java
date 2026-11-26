package com.lcy.yozoraticket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lcy.yozoraticket.enums.ErrorCode;
import com.lcy.yozoraticket.exception.BusinessException;
import com.lcy.yozoraticket.service.SuccessTicketCountService;
import com.lcy.yozoraticket.service.TicketBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketSuccessSuperService {

   private final TicketBuyService ticketBuyService;
   private final TicketServiceImpl ticketService;
   private final SuccessTicketCountService successTicketCountService;


    /**
     * 提交订单
     * @param orderNo
     */
   @Transactional
   public void success(String orderNo){
       //支付订单
       boolean flag = ticketBuyService.buyCommitTicket(orderNo);

       //订单支付成功消息组装
       try {
           successTicketCountService.TicketCountPersistence(orderNo);
       } catch (JsonProcessingException e) {
           throw new BusinessException(ErrorCode.JSON_DESERIALIZATION_ERROR);
       }

       if (flag) {
           //生成电子票
           ticketService.getTicket(orderNo);
       }


   }

}
