package com.lcy.yozoraticket.handler;


import com.lcy.yozoraticket.exception.*;
import com.lcy.yozoraticket.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 通用业务异常类
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException ex){
        return Result.error(ex.getMessage());
    }
}
