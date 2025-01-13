package com.virtual.cloud.om.quartz.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * per day cron resolver
 */
public class CronTimeMinutesResolver extends CronTimeResolver {
    private static final Logger logger = LoggerFactory.getLogger(CronTimeMinutesResolver.class);

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimeMinutesResolver(Integer cycleValue) {
        super( "", cycleValue);
    }

    @Override
    public String toCronExpress() {
        try {
            StringBuilder expressBuilder = new StringBuilder();
            expressBuilder.append("0").append(" ");    // sec
            expressBuilder.append("0/"+cycleValue.toString()).append(" ");    // min
            expressBuilder.append("*").append(" ");    // hour
            expressBuilder.append("*").append(" ");    // day
            expressBuilder.append("*").append(" ");    // month
            expressBuilder.append("?");    // week
            String express = expressBuilder.toString();
            logger.debug("RepeatedMinuteTimeResolver:: parse cycleValue={} to cron express={} successfully.", cycleValue.toString(), express);
            return express;
        } catch (Exception e) {
            logger.error("RepeatedMinuteTimeResolver:: parse cycleValue {} to cron express failed due to {}.", cycleValue.toString(), e);
            throw e;
        }
    }
}
