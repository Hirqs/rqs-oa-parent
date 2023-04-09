package com.rqs.common.exception;

import com.rqs.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    //全局异常处理，执行的方法
    @ExceptionHandler(Exception.class)
    @ResponseBody//@ResponseBody的作用其实是将java对象转为json格式的数据。
    public Result error() {
        return Result.fail().message("执行全局异常处理...");
    }
}
