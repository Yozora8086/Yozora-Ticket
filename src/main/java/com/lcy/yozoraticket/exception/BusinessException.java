package com.lcy.yozoraticket.exception;

import com.lcy.yozoraticket.enums.ErrorCode;
import lombok.Getter;

/**
 * 通用业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 通用业务异常类
     * @param errorCode
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
