package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.agent.dto.WarnStrategyDTO;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.agent.service.warn.WarnReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 实现乐观锁，字段为version，调度之前应查询version的值+nextFireTime（下次执行时间的值）
 */
@Slf4j
@DisallowConcurrentExecution
public class WarnReportJob extends AbstractJob{
    @Resource
    private WarnReportService warnReportService;

    @Override
    void doJob(Task task) {
        WarnStrategyDTO warnStrategyDTO = (WarnStrategyDTO)task.getData();
        if (Objects.nonNull(warnStrategyDTO)){
            String tags = warnStrategyDTO.getTags();
            String metric = warnStrategyDTO.getMetric();
            WarnMetricEnum warnMetricEnum = WarnMetricEnum.valueOf(metric);
            try {
                warnReportService.report(tags,warnMetricEnum);
            } catch (Exception e) {
                log.error("warn strategy is fail : " +e+" : "+tags);
            }
        }
    }
}
