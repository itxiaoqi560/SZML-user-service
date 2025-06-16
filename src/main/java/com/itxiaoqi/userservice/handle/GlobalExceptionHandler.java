package com.itxiaoqi.userservice.handle;

import com.itxiaoqi.userservice.entity.result.Result;
import com.itxiaoqi.userservice.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 捕获业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public Result businessExceptionHandler(BusinessException e){
        log.error("异常信息：{}", e.getMessage());
//        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    /**
     * 捕获系统异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Result exceptionHandler(RuntimeException e){
        log.error("异常信息：{}", e.getMessage());
//        e.printStackTrace();
        return Result.error(e.getMessage());
    }

}
