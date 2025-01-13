package com.virtual.cloud.om.sdk.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author w22798
 * @date 2022/0311
 * VDI RejectedExecutionHandler自定义实现类。当任务无法执行时，触发此handler。
 */
@Slf4j
public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.warn("queue full, {} rejected, {}", r, executor);
    }
}

