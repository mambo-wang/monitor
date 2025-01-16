package com.virtual.cloud.om.agent.task.cron;

/**
 * Created by z10894 on 2016/7/26.
 */
public abstract class CronDateTimeResolver implements CronResolver {

    /**
     * time format must be "yyyy-MM-dd HH:mm:ss"
     */
    final String datetime;

    /**
     * time format must be "yyyy-MM-dd HH:mm:ss"
     */
    public CronDateTimeResolver(String datetime) {
        this.datetime = datetime;
    }

}
