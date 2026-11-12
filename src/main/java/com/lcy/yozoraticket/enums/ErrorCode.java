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

    TICKET_SELL_ERROR(2004,"购票失败",HttpStatus.CONFLICT),

    TICKET_LOCK_ERROR(2005,"取消冻结的票数量大于被冻结的票数量",HttpStatus.CONFLICT),

    TICKET_NOT_HAS(2006,"当前演出所属的票档不存在",HttpStatus.CONFLICT),

    TICKET_NOT_YOU(3001,"当前电子票归属用户错误",HttpStatus.CONFLICT),

    ORDER_NOT_YOU(3002,"当前订单归属用户错误",HttpStatus.CONFLICT),

    ORDER_STATUS_IS_PAY_OR_TIMEOUT(4001,"当前订单已支付或者已取消",HttpStatus.CONFLICT),

    ORDER_PAY_TIME_CLOSE(4002,"当前订单已超时",HttpStatus.CONFLICT),

    JSON_DESERIALIZATION_ERROR(5001,"Json反序列化错误",HttpStatus.CONFLICT),

    JSON_SERIALIZATION_ERROR(5001,"Json序列化错误",HttpStatus.CONFLICT);


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
