package com.virtual.cloud.om.agent.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by z10894 on 2016/7/26.
 */
public class CronTimePerMonthResolver extends CronTimeResolver {
    private static final Logger logger = LoggerFactory.getLogger(CronTimePerMonthResolver.class);

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimePerMonthResolver(final String time, final Integer cycleValue) {
        super(time, cycleValue);
    }

    @Override
    public String toCronExpress() {
        try {
            Objects.requireNonNull(time);
            Objects.requireNonNull(cycleValue);

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
            cronExpress.append(cycleValue).append(" ");    // day
            cronExpress.append("*").append(" ");    // month
            cronExpress.append("?");    // week

            String express = cronExpress.toString();
            logger.debug("parse time={}, cycle value={} to cron express={}", time, cycleValue, express);

            return express;
        } catch (Exception e) {
            logger.warn("time {} parse to cron express failure", time, e);
        }
        return null;
    }
}
