package com.virtual.cloud.om.sdk.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 任务执行的线程池
 *
 * 使用示例，一次提交任务数不要超过任务队列长度：
 *         ExecutorService service = VDIExecutorServices.get().getCommonService();
 *         IntStream.rangeClosed(1,10).forEach(i -> service.execute(() -> System.out.println(i)));
 */
@Slf4j
public class CloudExecutorServices {

    private static final CloudExecutorServices instance = new CloudExecutorServices();

    /** 缺省的核心线程数 */
    private static final int THREAD_CORE = 6;

    /** 缺省的最大线程数 */
    private static final int THREAD_MAX = 12;

    /** 缺省的空闲间隔 */
    private static final int THREAD_IDLE = 30;

    /** 缺省的队列大小 */
    private static final int THREAD_CAPACITY = 1024;

    private final int CORE = Runtime.getRuntime().availableProcessors();

    public static CloudExecutorServices get() {
        return instance;
    }

    //用于处理IO阻塞类型的线程池，占用CPU资源较少，但是占用时间长，如RPC调用，文件上传下载。
    private volatile ExecutorService ioBusyService = null;

    //用于处理CPU繁忙型工作，如数值计算等
    private volatile ExecutorService cpuBusyService = null;

    //用于自定义参数的线程池
    private volatile ExecutorService customizeService = null;

    //公共事件线程池，如异步入库、异步方法执行等
    private volatile ExecutorService commonService = null;

    // 启监控线程
    private volatile ExecutorService monitorService = null;

    // Filebeat运行状态检测
    private volatile ExecutorService filebeatService = null;

    // Filebeat主机运行状态检测
    private volatile ExecutorService filebeatTargetService = null;

    //kafka消费线程池（目前3个线程）
    private volatile ExecutorService kafkaService = null;

    public ExecutorService getCpuBusyService() {
        if (null == this.cpuBusyService) {
            synchronized (this) {
                if (null == this.cpuBusyService) {
                    cpuBusyService = new ThreadPoolExecutor(CORE>THREAD_CORE?CORE+1:THREAD_CORE,
                            CORE*2>THREAD_MAX?CORE*2:THREAD_MAX, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(THREAD_CAPACITY),
                            new DaemonThreadFactory("WebCommonOperateService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return cpuBusyService;
    }

    public ExecutorService getCustomizeService(int corePoolSize, int maximumPoolSize, int queueSize) {
        if (this.customizeService == null) {
            synchronized (this) {
                if (this.customizeService == null) {
                    log.info("the core thread num of cluster is {}", CORE);
                    customizeService = new RadicalThreadPoolExecutor(
                            corePoolSize,
                            maximumPoolSize,
                            THREAD_IDLE,
                            TimeUnit.SECONDS,
                            queueSize);
                }
            }
        }
        return customizeService;
    }

    public ExecutorService getIoBusyService() {
        if (this.ioBusyService == null) {
            synchronized (this) {
                if (this.ioBusyService == null) {
                    log.info("the core thread num of cluster is {}", CORE);
                    ioBusyService = new RadicalThreadPoolExecutor(
                            CORE * 10,
                            CORE * 20,
                            THREAD_IDLE,
                            TimeUnit.SECONDS,
                            THREAD_CAPACITY);
                }
            }
        }
        return ioBusyService;
    }

    /**
     * 通用线程池，占用线程数不多
     */
    public ExecutorService getCommonService() {
        if (null == commonService) {
            synchronized (this) {
                if (null == commonService) {
                    commonService = new ThreadPoolExecutor(CORE,
                            CORE * 2, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(THREAD_CAPACITY),
                            new DaemonThreadFactory("WebCommonService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return commonService;
    }

    /**
     * kafka消费线程池，目前有三个线程
     */
    public ExecutorService getKafkaService() {
        if (null == kafkaService) {
            synchronized (this) {
                if (null == kafkaService) {
                    kafkaService = new ThreadPoolExecutor(3,
                            6, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(1),
                            new DaemonThreadFactory("KafkaService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return kafkaService;
    }

    public ExecutorService getMonitorService() {
        if (null == monitorService) {
            synchronized (this) {
                if (null == monitorService) {
                    monitorService = new ThreadPoolExecutor(CORE*2,
                            CORE * 4, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(THREAD_CAPACITY),
                            new DaemonThreadFactory("WebMonitorService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return monitorService;
    }

    public ExecutorService getFilebeatService() {
        if (null == filebeatService) {
            synchronized (this) {
                if (null == filebeatService) {
                    filebeatService = new ThreadPoolExecutor(CORE*2,
                            CORE * 4, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(THREAD_CAPACITY),
                            new DaemonThreadFactory("FilebeatCheckService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return filebeatService;
    }

    public ExecutorService getFilebeatTargetService() {
        if (null == filebeatTargetService) {
            synchronized (this) {
                if (null == filebeatTargetService) {
                    filebeatTargetService = new ThreadPoolExecutor(CORE*2,
                            CORE * 4, THREAD_IDLE, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(THREAD_CAPACITY),
                            new DaemonThreadFactory("FilebeatTargetCheckService"),
                            new RejectedExecutionHandlerImpl());
                }
            }
        }
        return filebeatTargetService;
    }
}
