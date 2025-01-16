package com.virtual.cloud.om.agent.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * per day cron resolver
 */
public class CronTimePerDayResolver extends CronTimeResolver {
    private static final Logger logger = LoggerFactory.getLogger(CronTimePerDayResolver.class);

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimePerDayResolver(final String time) {
        super(time, 0);
    }

    @Override
    public String toCronExpress() {
        try {
            Objects.requireNonNull(time);

            StringBuilder cronExpress = new StringBuilder();
            String value = time.trim();

            String[] timeArr = value.split(TIME_SEGMENTATION);
            String hour = timeArr[0];
            String minute = timeArr[1];
            String second = timeArr[2];

            // parse time
            cronExpress.append(second).append(" ");
            cronExpress.append(minute).append(" ");
            cronExpress.append(hour).append(" ");

            // parse date
            cronExpress.append("*").append(" ");    // day
            cronExpress.append("*").append(" ");    // month
            cronExpress.append("?");    // week

            String express = cronExpress.toString();
            logger.debug("parse time={} to cron express={}", time, express);

            return express;
        } catch (Exception e) {
            logger.warn("time {} parse to cron express failure", time, e);
        }

        return null;
    }
}
