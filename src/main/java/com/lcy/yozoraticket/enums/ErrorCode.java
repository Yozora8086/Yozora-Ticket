package com.lcy.yozoraticket.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    LOGIN_SESSION_ERROR(1001,"登录session不符",HttpStatus.UNAUTHORIZED),

    NO_LOGIN(1002,"用户未登录",HttpStatus.UNAUTHORIZED),

    UNKNOW_TOKEN(1003,"Token无效",HttpStatus.UNAUTHORIZED),

    NOT_TICKET(2001,"票库存不足",HttpStatus.CONFLICT),

    TICKET_TIME_NOT_SELL(2002,"当前演出未达发售时间",HttpStatus.CONFLICT),

    TICKET_STATUS_NOT_SELL(2003,"当前演出已取消，无法购买",HttpStatus.CONFLICT),

    TICKET_SELL_ERROR(2004,"购票失败",HttpStatus.CONFLICT);


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
