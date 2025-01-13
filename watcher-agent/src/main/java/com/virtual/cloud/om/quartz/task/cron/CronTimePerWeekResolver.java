package com.virtual.cloud.om.quartz.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 */
public class CronTimePerWeekResolver extends CronTimeResolver {
    private static final Logger logger = LoggerFactory.getLogger(CronTimePerWeekResolver.class);

    /**
     * time format must be "HH:mm:ss"
     */
    public CronTimePerWeekResolver(final String time, final Integer cycleValue) {
        super(time, cycleValue);
    }

    @Override
    public String toCronExpress() {
        List<String> week = new ArrayList<>();
        week.add("SUN");
        week.add("MON");
        week.add("TUE");
        week.add("WED");
        week.add("THU");
        week.add("FRI");
        week.add("SAT");
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
            cronExpress.append("?").append(" ");    // day
            cronExpress.append("*").append(" ");    // month
            cronExpress.append(week.get(cycleValue == 7 ? 0 : cycleValue));    // week

            String express = cronExpress.toString();
            logger.debug("parse time={}, cycle value={} to cron express={}", time, cycleValue, express);

            return express;
        } catch (Exception e) {
            logger.warn("time {} parse to cron express failure", time, e);
            throw e;
        }
    }
}
