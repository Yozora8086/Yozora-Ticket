package com.lcy.yozoraticket.handler;


import com.lcy.yozoraticket.exception.LoginSessionErrorException;
import com.lcy.yozoraticket.exception.NoLoginException;
import com.lcy.yozoraticket.exception.TicketIsNotSellException;
import com.lcy.yozoraticket.exception.UnauthorizedException;
import com.lcy.yozoraticket.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * user未登录异常
     * @param ex
     * @return
     */
    @ExceptionHandler(NoLoginException.class)
    public Result handleNoLoginException(NoLoginException ex){
        return Result.error("user当前未登录");
    }

    /**
     * Token无效异常
     * @param ex
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result handleUnauthorizedException(UnauthorizedException ex){
        return Result.error("Token无效");
    }


    /**
     * 登录session不符异常
     * @param ex
     * @return
     */
    @ExceptionHandler(LoginSessionErrorException.class)
    public Result handleLoginSessionErrorException(LoginSessionErrorException ex){
        return Result.error("该账号已在其他设备登录");
    }

    /**
     * 演出票未到发售时间异常
     * @param ex
     * @return
     */
    @ExceptionHandler(TicketIsNotSellException.class)
    public Result handleTicketIsNotSellException(TicketIsNotSellException ex){
        return Result.error(ex.getMessage());
    }
}
