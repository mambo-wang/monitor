package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.dto.RpcResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 将业务异常统一返回 state=1，便于前端判断
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public RpcResult<Void> handleRuntimeException(RuntimeException e) {
        return RpcResult.fail(e.getMessage());
    }
}
