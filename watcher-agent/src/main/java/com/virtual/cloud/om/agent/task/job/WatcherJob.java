package com.virtual.cloud.om.agent.task.job;

import com.virtual.cloud.om.agent.dto.StrategyDTO;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.service.report.DataReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/5/5 14:40
 */

/**
 * 实现乐观锁，字段为version，调度之前应查询version的值+nextFireTime（下次执行时间的值）
 */
@Slf4j
@DisallowConcurrentExecution
public class WatcherJob extends AbstractJob{
    @Resource
    private DataReportService dataReportService;

    @Override
    void doJob(Task task) {
        StrategyDTO StrategyDTO = (StrategyDTO)task.getData();
        if (Objects.nonNull(StrategyDTO)){
            String tags = StrategyDTO.getTags();
            String metric = StrategyDTO.getMetric();
            log.info("【task begin】tags={},metric={}",tags,metric);
            try {
                dataReportService.report(tags,metric);
            } catch (Exception e) {
                log.error("Strategy is fail : " +e+" : "+tags);
            }
            log.info("【task finish】tags={},metric={}",tags,metric);
        }
    }
}
