package com.virtual.cloud.om.quartz.task.cron;

/**
 * Cron express parser
 */

public interface CronResolver {
    /**
     * time segmentation ":", because time format is HH:mm:ss
     */
    String TIME_SEGMENTATION = ":";

    /**
     * date segmentation "-", because date format is yyyy-MM-dd
     */
    String DATE_SEGMENTATION = "-";
    /**
     * parse to cron express
     * @return
     */
    String toCronExpress();
}
