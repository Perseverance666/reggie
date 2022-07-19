package com.example.reggie.common;

/**
 * 自定义业务异常类
 * @Date: 2022/6/11 18:01
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
