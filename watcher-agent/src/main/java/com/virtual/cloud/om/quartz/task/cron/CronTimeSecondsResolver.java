package com.virtual.cloud.om.quartz.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * per day cron resolver
 */
public class CronTimeSecondsResolver extends CronTimeResolver {
    private static final Logger log = LoggerFactory.getLogger(CronTimeSecondsResolver.class);

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimeSecondsResolver(Integer cycleValue) {
        super( "", cycleValue);
    }

    @Override
    public String toCronExpress() {
        try {
            StringBuilder expressBuilder = new StringBuilder();
            expressBuilder.append("0/" + cycleValue.toString()).append(" ");    // second
            expressBuilder.append("*").append(" ");    // min
            expressBuilder.append("*").append(" ");    // hour
            expressBuilder.append("*").append(" ");    // day
            expressBuilder.append("*").append(" ");    // month
            expressBuilder.append("?");    // week
            String express = expressBuilder.toString();
            log.debug("RepeatedSecondTimeResolver:: parse cycleValue={} to cron express={} successfully.", cycleValue.toString(), express);
            return express;
        } catch (Exception e) {
            log.error("RepeatedSecondTimeResolver:: parse cycleValue {} to cron express failed due to {}.", cycleValue.toString(), e);
            throw e;
        }
    }
}
