package com.virtual.cloud.om.quartz.task.cron;

/**
 * Created by z10894 on 2016/7/26.
 */
public abstract class CronTimeResolver implements CronResolver {
    /**
     * time format must be "HH:mm:ss"
     */
    final String time;
    final Integer cycleValue;

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimeResolver(String time, Integer cycleValue) {
        this.cycleValue = cycleValue;
        this.time = time;
    }
}
