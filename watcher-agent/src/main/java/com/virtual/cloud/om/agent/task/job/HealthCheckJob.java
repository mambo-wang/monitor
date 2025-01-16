package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.sdk.api.DeployApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author:XK
 * @Date:2022/5/5 14:40
 */

@Component
@Slf4j
public class HealthCheckJob{

    private DeployApi deployApi;

    @Autowired
    public void setDeployApi(DeployApi deployApi) {
        this.deployApi = deployApi;
    }

    /**
     * 健康检查任务使用Scheduled 每2分钟触发一次
     */
    @Async
    @Scheduled(initialDelay = 60*1000, fixedRate = 120 * 1000)
    public void healthCheck() {
        deployApi.handleHealthCheck();
    }
}
