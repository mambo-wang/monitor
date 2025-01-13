package com.virtual.cloud.om.quartz.task.job;

import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.utils.SpringContextsUtil;
import org.quartz.DisallowConcurrentExecution;

/**
 * @author:hanfeiyang
 * @Date:2022/7/19 17.34
 */

/**
 * 实现乐观锁，字段为version，调度之前应查询version的值+nextFireTime（下次执行时间的值）
 */
@DisallowConcurrentExecution
public class FilebeatCheckJob extends AbstractJob{

    RealTimeLogApi realTimeLogApi;

    FilebeatCheckJob() {
        realTimeLogApi = (RealTimeLogApi) SpringContextsUtil.getBean("realTimeLogApi");
    }

    @Override
    void doJob(Task task) {
        realTimeLogApi.handleFilebeatCheck();
    }
}
