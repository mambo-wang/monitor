package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.agent.entity.Task;
import org.apache.commons.lang3.StringUtils;

public class JobClassFactory {

    public static Class getJobClass(final String taskType) {
        if (StringUtils.equals(Task.TASK_STRATEGY_ISSUE, taskType)) {
            return WatcherJob.class;
        } else if (StringUtils.equals(Task.TASK_FILEBEAT_CHECK, taskType)) {
            return FilebeatCheckJob.class;
        } else {
            return WatcherJob.class;
        }
    }
}
