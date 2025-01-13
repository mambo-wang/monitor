package com.virtual.cloud.om.quartz.task.cron;


import com.virtual.cloud.om.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by z10894 on 2016/7/26.
 */
@Component
public class CronResolverFactory {
    private static final Logger logger = LoggerFactory.getLogger(CronResolverFactory.class);

    /**
     * generate each type cron resolver
     * @param cycleType task cycle type
     * @param time  task execute time. The time format should be "yyyy-MM-dd HH:mm:ss" or "HH:mm:ss" based on the task cycle type
     * @param cycleValue    task cycle value. If no need recycle or cycle type is every day, the parameter should be null
     * @return
     */
    public static CronResolver generateCronResolver(final String cycleType, final String time,
                                                    final Integer cycleValue) {
        if (Task.CYCLE_TYPE_ONCE.equals(cycleType) ) {
            return new CronDateTimeOnceResolver(time);  // time format must be "yyyy-MM-dd HH:mm:ss"
        } else if (Task.CYCLE_TYPE_EVERYDAY.equals(cycleType) ) {
            return new CronTimePerDayResolver(time);    // time format must be "HH:mm:ss"
        } else if (Task.CYCLE_TYPE_EVERYMONTH.equals(cycleType)) {
            return new CronTimePerMonthResolver(time, cycleValue);  // time format must be "HH:mm:ss"
        } else if (Task.CYCLE_TYPE_EVERYWEEK.equals(cycleType)) {
            return new CronTimePerWeekResolver(time, cycleValue);   // time format must be "HH:mm:ss"
        } else if (Task.CYCLE_TYPE_HOUR.equals(cycleType)) {
            return new CronTimeHourResolver(cycleValue);   // time format must be "HH:mm:ss"
        } else if (Task.CYCLE_TYPE_MINUTES.equals(cycleType)) {
            return new CronTimeMinutesResolver(cycleValue);
        } else if(Task.CYCLE_TYPE_SECOND.equals(cycleType)){
            return new CronTimeSecondsResolver(cycleValue);
        } else {
            logger.warn("unknown task cycle type={} found and time={}, cycleValue={}", cycleType, time, cycleValue);
            return null;
        }
    }
}
