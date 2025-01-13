package com.virtual.cloud.om.sdk.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 *  @author w22798
 *  @date 2022/0311
 * VDI ThreadFactory自定义实现类
 */
public class DaemonThreadFactory implements ThreadFactory {

    private String threadName = "WatcherDaemonThread";

    public DaemonThreadFactory() {
    }

    public DaemonThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        if (this.threadName != null) {
            thread.setName(this.threadName + "-" + thread.getId());
        }
        return thread;
    }
}
