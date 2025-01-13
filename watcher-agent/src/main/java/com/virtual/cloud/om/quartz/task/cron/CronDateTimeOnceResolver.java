package com.virtual.cloud.om.quartz.task.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by z10894 on 2016/7/26.
 */
public class CronDateTimeOnceResolver extends CronDateTimeResolver {
    private static final Logger logger = LoggerFactory.getLogger(CronDateTimeOnceResolver.class);

    /**
     * time format must be "yyyy-MM-dd HH:mm:ss"
     */
    public CronDateTimeOnceResolver(final String datetime) {
        super(datetime);
    }

    @Override
    public String toCronExpress() {
        try {
            Objects.requireNonNull(datetime);

            StringBuilder cron = new StringBuilder();
            String value = datetime.trim();
            // split date and time
            String[] datetimeArr = value.split(" ");
            String date = datetimeArr[0];
            String time = datetimeArr[1];

            // parse time
            String[] timeItems = time.split(TIME_SEGMENTATION);
            String hour = timeItems[0];
            String minute = timeItems[1];
            String second = timeItems[2];
            cron.append(second).append(" ");    // second
            cron.append(minute).append(" ");    // minute
            cron.append(hour).append(" ");      // hour

            // parse date
            String[] dateItems = date.split(DATE_SEGMENTATION);
            String year = dateItems[0];
            String month = dateItems[1];
            String day = dateItems[2];
            cron.append(day).append(" ");   // day
            cron.append(month).append(" "); // month
            cron.append("?").append(" ");   // week
            cron.append(year).append(" ");  // year

            String express = cron.toString();
            logger.debug("parse datetime={} to cron express={}", datetime, express);

            return express;
        } catch (Exception e) {
            logger.warn("parse datetime {} to cron failure", datetime, e);
        }
        return null;
    }
}
