package com.lcy.yozoraticket.service;

public interface TicketService {
    /**
     * 生成演出电子票
     * @param orderNo
     */
    void getTicket(String orderNo);

    /**
     * 核销演出电子票
     * @param orderNo
     */
    void useTicket(String orderNo);
}
