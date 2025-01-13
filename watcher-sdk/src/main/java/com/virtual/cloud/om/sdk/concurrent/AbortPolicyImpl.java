package com.virtual.cloud.om.sdk.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author w22798
 * @date 2022/0311
 */
@Slf4j
public class AbortPolicyImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.warn("queue full, {} rejected, {}", r, e);
        throw new RejectedExecutionException("当前系统繁忙，请稍后再试。");
    }
}
