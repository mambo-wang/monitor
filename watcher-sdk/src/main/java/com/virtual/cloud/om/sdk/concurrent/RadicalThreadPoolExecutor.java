package com.virtual.cloud.om.sdk.concurrent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description 自定义激进线程池，在核心线程数满了后，新的任务来了，直接创建线程，直到达到最大线程数，此时再新来任务，此时再加入阻塞队列
 * 使用场景：我们希望的是尽量确保有足够多的线程能处理任务（IO密集型），但是又不闲置过多线程，或临时创建过多线程，换句话说让线程的创建和回收不要太频繁
 * @author: w22798
 * @data: 2020-08-23 15:19
 */
@Slf4j
public class RadicalThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 构造方法
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 非核心线程数保留时长
     * @param unit 非核心线程数保留时长单位
     * @param blockQueueSize 阻塞队列长度
     */
    public RadicalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int blockQueueSize) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new RadicalBlockQueue(blockQueueSize), new DaemonThreadFactory("WatcherAsyncService"), new RadicalRejectedPolicy());

    }

    /**
     * 自定义阻塞队列
     * @param <Runnable>
     */
    static class RadicalBlockQueue<Runnable> extends LinkedBlockingQueue<Runnable> {
        private static final long serialVersionUID = 4780193248779924327L;

        public RadicalBlockQueue(int capacity) {
            super(capacity);
        }

        /**
         * 覆盖默认的offer方法，触发拒绝策略执行
         * @param runnable
         * @return
         */
        @Override
        public boolean offer(Runnable runnable) {
            return false;
        }
    }

    /**
     * 自定义拒绝策略
     */
    static class RadicalRejectedPolicy implements RejectedExecutionHandler {

        private static final Logger log = LoggerFactory.getLogger(RadicalRejectedPolicy.class);

        @SneakyThrows
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            //线程池非关闭
            if (!e.isShutdown()) {
                //真正入阻塞队列，若阻塞队列已满，则抛出RejectedExecutionException
                if (!e.getQueue().offer(r, 1, TimeUnit.SECONDS)) {
                    //可以真正进行触发策略的执行（不管是默认的，还是自定义的）
                    log.error("Task " + r.toString() + " rejected from " + e.toString());
                }
            }
        }
    }
}
