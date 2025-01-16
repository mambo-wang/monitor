package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.agent.service.warn.WarnStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;

import javax.annotation.Resource;

@DisallowConcurrentExecution
@Slf4j
public class PullWarnStrategyJob extends AbstractJob{

    @Resource
    private WarnStrategyService warnStrategyService;

    @Override
    void doJob(Task task) {

        CloudExecutorServices.get().getMonitorService().execute(() -> warnStrategyService.pullWarnStrategyFromDataCenter());
    }
}
