package com.lcy.yozoraticket.exception;

/**
 * 演出票未到发售时间异常
 */
public class TicketIsNotSellException extends RuntimeException {
    public TicketIsNotSellException(String msg) {super(msg);}
}
