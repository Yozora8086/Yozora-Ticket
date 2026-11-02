package com.lcy.yozoraticket.service.impl;

import com.lcy.yozoraticket.service.TicketBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketSuccessSuperService {

   private final TicketBuyService ticketBuyService;
   private final TicketServiceImpl ticketService;

    /**
     * 提交订单
     * @param orderNo
     */
   @Transactional
   public void success(String orderNo){
       //支付订单
       boolean flag = ticketBuyService.buyCommitTicket(orderNo);

       if (flag) {
           //生成电子票
           ticketService.getTicket(orderNo);
       }
   }

}
