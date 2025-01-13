package com.virtual.cloud.om.quartz.task.job;

import cn.hutool.core.collection.CollUtil;
import com.virtual.cloud.om.entity.Task;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.api.ResourceApi;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.service.resource.ResourceService;
import com.virtual.cloud.om.service.strategy.StrategyService;
import org.quartz.DisallowConcurrentExecution;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author:XK
 * @Date:2022/5/12 16:15
 */
@DisallowConcurrentExecution
@Slf4j
public class PullStrategyJob extends AbstractJob{

    @Resource
    private StrategyService strategyService;

    @Resource
    private ResourceApi resourceApi;

    @Resource
    private RealTimeLogApi realTimeLogApi;

    @Override
    void doJob(Task task) {
        //先获取最新资源列表，在分别执行数据收集策略和日志采集策略
        if(CollUtil.isNotEmpty(this.resourceApi.findAll())){
        }
    }
}
