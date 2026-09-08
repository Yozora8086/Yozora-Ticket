package com.lcy.yozoraticket.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private T data;
    private String message;


    public static <T> Result<T> success(String message){
        Result<T> result = new Result<>();
        result.code = 1;
        result.message = message;
        return result;
    }

    public static <T> Result<T> success(T o,String message){
        Result<T> result = new Result<T>();
        result.data = o;
        result.code = 1;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(String message){
        Result<T> result = new Result<T>();
        result.code = 0;
        result.message = message;
        return result;
    }

}
