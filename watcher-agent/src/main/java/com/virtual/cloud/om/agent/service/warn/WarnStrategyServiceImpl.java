package com.virtual.cloud.om.agent.service.warn;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import com.virtual.cloud.om.agent.dto.WarnStrategyDTO;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.agent.service.deploy.DeployService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WarnStrategyServiceImpl implements WarnStrategyService, ApplicationRunner {

    public final Integer frequency = 30;//todo 为方便调试，频率设置的比较高

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private TaskMgrApi taskMgrApi;

    @Resource
    private DataCenterApi DataCenterApi;

    @Resource
    private DeployService deployService;

    @Resource
    private WarnReportService warnReportService;

    @Resource
    private DeployApi deployApi;

    @PostConstruct
    public void init() {
        /**
         * 主节点启动执行一次拉取
         */
        try {
            boolean isMaster = deployApi.currentNodeIsMaster();
            if (isMaster) {
                this.WarnStrategyPullTask();
                CloudExecutorServices.get().getCommonService().execute(() -> this.pullWarnStrategyFromDataCenter());
                log.info("O.o<WarnStrategyFromDataCenter>o.O pullWarnStrategyFromDataCenter init finish");
            }
        } catch (Exception e) {
            log.error("生成告警策略拉取定时任务失败: " + e);
        }
    }

    /**
     * 定时策略拉取
     */
    public void WarnStrategyPullTask() {
        TaskDTO task = new TaskDTO();
        task.setId("warn_pull_task")
                .setTaskName("warn_pull_task")
                .setAvailableStartTime(System.currentTimeMillis())
                .setCycleType(Task.CYCLE_TYPE_MINUTES)
                .setCycleDay(frequency)
                .setTaskType(Task.TASK_WARN_PULL)
                .setDescription("告警策略拉取")
                .setCreatedTime(Utils.formatFullDateTime(System.currentTimeMillis()));
        taskMgrApi.addTask(task);
    }


    @Override
    public void pullWarnStrategyFromDataCenter() {

    }

    public void taskserverForPull(List<WarnStrategyDTO> strategyDTOS) {
        log.info("告警策略: " + strategyDTOS);
        if (CollectionUtil.isEmpty(strategyDTOS)) {
            taskMgrApi.deleteAllStrategyTask(Task.TASK_WARN_STRATEGY_ISSUE);
            log.info(" warn strategy is empty");
            return;
        }
        List<WarnStrategyDTO> onceStrategys = strategyDTOS.stream().filter(s -> Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        List<WarnStrategyDTO> scheduleStrategiesFromDataCenter = strategyDTOS.stream().filter(s -> !Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        this.onceTask(onceStrategys);
        List<TaskDTO> tasksFromDatabase = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_WARN_STRATEGY_ISSUE);
        if (CollectionUtil.isEmpty(tasksFromDatabase)) {
            this.taskGroup(scheduleStrategiesFromDataCenter);
            log.info("first insert warn scheduleStrategiesFromDataCenter :" + scheduleStrategiesFromDataCenter);
            return;
        }
        Map<String, List<WarnStrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
        Map<String, List<TaskDTO>> resourceIdTaskMap = tasksFromDatabase.parallelStream().collect(Collectors.groupingBy(taskDTO -> taskDTO.getResourceId()));
        /**
         * 主动拉取的时候，根据resourceId去删除定时任务
         */
        List<String> deleteResourceId = resourceIdTaskMap.keySet().stream().filter(id -> CollectionUtil.isEmpty(resourceIdStrategyMap.get(id))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(deleteResourceId)) {
            List<TaskDTO> deleteTask = deleteResourceId.stream().map(d -> resourceIdTaskMap.get(d)).flatMap(Collection::stream).collect(Collectors.toList());
            deleteTask.stream().forEach(t -> taskMgrApi.delete(t));
        }
        Set<String> resouceIdFromCenter = resourceIdStrategyMap.keySet();
        resouceIdFromCenter.stream().forEach(r -> {
            try {
                List<TaskDTO> taskDTOList = resourceIdTaskMap.get(r);
                List<WarnStrategyDTO> strategyDTOListCenter = resourceIdStrategyMap.get(r);
                if (CollectionUtil.isNotEmpty(taskDTOList)) {
                    Map<String, WarnStrategyDTO> scheduleStrategiesMapFromDataCenter = strategyDTOListCenter.stream().collect(Collectors.toMap(WarnStrategyDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    Map<String, TaskDTO> taskMapFromDatabase = taskDTOList.stream().collect(Collectors.toMap(TaskDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    this.deleteTask(taskDTOList, scheduleStrategiesMapFromDataCenter);
                    this.insertTaskCompare(strategyDTOListCenter, taskMapFromDatabase);
                } else {
                    log.info("new insert warn resouceId:{} to task ", r);
                    this.taskGroup(strategyDTOListCenter);
                }
            } catch (Exception e) {
                log.error("fail to compare warn resouceId= " + r);
            }
        });

    }

    @Override
    public void taskserver(List<WarnStrategyDTO> warnStrategyDTOS) {
        log.info("告警策略: " + warnStrategyDTOS);
        if (CollectionUtil.isEmpty(warnStrategyDTOS)) {
            taskMgrApi.deleteAllStrategyTask(Task.TASK_WARN_STRATEGY_ISSUE);
            log.info("warn strategy is empty");
            return;
        }
        List<WarnStrategyDTO> onceStrategys = warnStrategyDTOS.stream().filter(s -> Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        List<WarnStrategyDTO> scheduleStrategiesFromDataCenter = warnStrategyDTOS.stream().filter(s -> !Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        this.onceTask(onceStrategys);
        List<TaskDTO> tasksFromDatabase = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_WARN_STRATEGY_ISSUE);
        if (CollectionUtil.isEmpty(tasksFromDatabase)) {
            this.taskGroup(scheduleStrategiesFromDataCenter);
            log.info("first insert warn scheduleStrategiesFromDataCenter :" + scheduleStrategiesFromDataCenter);
            return;
        }
        Map<String, List<WarnStrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
        Map<String, List<TaskDTO>> resourceIdTaskMap = tasksFromDatabase.parallelStream().collect(Collectors.groupingBy(taskDTO -> taskDTO.getResourceId()));
        Set<String> resouceIdFromCenter = resourceIdStrategyMap.keySet();
        resouceIdFromCenter.stream().forEach(r -> {
            try {
                List<TaskDTO> taskDTOList = resourceIdTaskMap.get(r);
                List<WarnStrategyDTO> strategyDTOListCenter = resourceIdStrategyMap.get(r);
                if (CollectionUtil.isNotEmpty(taskDTOList)) {
                    Map<String, WarnStrategyDTO> scheduleStrategiesMapFromDataCenter = strategyDTOListCenter.stream().collect(Collectors.toMap(WarnStrategyDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    Map<String, TaskDTO> taskMapFromDatabase = taskDTOList.stream().collect(Collectors.toMap(TaskDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    this.deleteTask(taskDTOList, scheduleStrategiesMapFromDataCenter);
                    this.insertTaskCompare(strategyDTOListCenter, taskMapFromDatabase);
                } else {
                    log.info("new insert warn resouceId:{} to task ", r);
                    this.taskGroup(strategyDTOListCenter);
                }
            } catch (Exception e) {
                log.error("fail to compare warn resouceId= " + r);
            }
        });


    }

    /**
     * 执行一次的直接调取接口
     *
     * @param onceStrategys
     */
    private void onceTask(List<WarnStrategyDTO> onceStrategys) {
        onceStrategys.stream().forEach(s -> {
            String metric = s.getMetric();
            WarnMetricEnum warnMetricEnum = WarnMetricEnum.valueOf(metric);
            try {
                warnReportService.report(s.getTags(), warnMetricEnum);
            } catch (Exception e) {
                log.error("warn OnceStrategys report fail : " + s + " : " + e);
            }
        });

    }


    /**
     * 获取需要添加的定时任务
     */
    private void insertTaskCompare(List<WarnStrategyDTO> scheduleStrategiesFromDataCenter, Map<String, TaskDTO> taskMapFromDatabase) {
        //找到需要新增的定时任务
        List<WarnStrategyDTO> insertTaskList = null;
        if (Objects.isNull(taskMapFromDatabase)) {
            insertTaskList = scheduleStrategiesFromDataCenter.stream().filter(c -> StringUtils.isNotEmpty(c.getHash())).collect(Collectors.toList());
        } else {
            insertTaskList = scheduleStrategiesFromDataCenter.stream().filter(s ->
                    !taskMapFromDatabase.containsKey(s.getHash()) && StringUtils.isNotEmpty(s.getHash())
            ).collect(Collectors.toList());
        }
        this.taskGroup(insertTaskList);
    }

    private void insertTask(List<WarnStrategyDTO> scheduleStrategiesFromDataCenter) {
        scheduleStrategiesFromDataCenter.stream().forEach(s -> {
            Optional<String> first = DataReportCollector.getId("resourceId", s.getTags()).stream().findFirst();
            if (first.isPresent()) {
                String resouceId = first.get();
                String taskId = this.createTaskId(resouceId, s.getMetric());
                if (Objects.nonNull(taskId)) {
                    TaskDTO task = new TaskDTO();
                    task.setResourceId(resouceId);
                    task.setHash(s.getHash());
                    task.setId(taskId);
                    task.setTaskName(s.getHash());
                    task.setTaskType(Task.TASK_WARN_STRATEGY_ISSUE);
                    task.setCycleType(s.getUnit());
                    task.setCycleDay(s.getFrequency());
                    task.setData(s);
                    task.setCreatedTime(Utils.formatFullDateTime(System.currentTimeMillis()));
                    task.setAvailableStartTime(System.currentTimeMillis());
                    task.setDescription(s.getPlatform() + "告警策略采集指标：" + s.getMetric());
                    taskMgrApi.addTask(task);
                    log.info("[INSERT_WARN_TASK]" + s.getPlatform() + "告警策略采集指标：" + s.getMetric() + " 定时任务已添加,tags= {}", s.getTags());
                }

            }
        });
    }

    /**
     * @param scheduleStrategiesFromDataCenter
     */
    private void taskGroup(List<WarnStrategyDTO> scheduleStrategiesFromDataCenter) {
        this.onceTask(scheduleStrategiesFromDataCenter);
        log.info("new insert task : " + scheduleStrategiesFromDataCenter);
        Map<String, List<WarnStrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
        Set<String> resourceIds = resourceIdStrategyMap.keySet();
        resourceIds.stream().forEach(r -> {
            List<WarnStrategyDTO> strategyDTOList = resourceIdStrategyMap.get(r);
            this.insertTask(strategyDTOList);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error("[sleep] 10 s fail");
            }
            log.info("[sleep] 10s success>>>>WARN<<<<");
        });
    }

    private void deleteTask(List<TaskDTO> tasksFromDatabase, Map<String, WarnStrategyDTO> scheduleStrategiesMapFromDataCenter) {
        List<TaskDTO> deleteTaskList = tasksFromDatabase.stream().filter(t -> !scheduleStrategiesMapFromDataCenter.containsKey(t.getHash())).collect(Collectors.toList());
        log.info("delete warn task ：" + deleteTaskList);
        deleteTaskList.stream().forEach(t -> taskMgrApi.delete(t));
    }

    private String createTaskId(String resourceId, String metric) {
        if (Objects.nonNull(resourceId) && Objects.nonNull(metric)) {
            String metricReplace = metric.replace(";", "");
            String id = resourceId + metricReplace;
            String md5 = SecureUtil.md5(id);
            return md5;
        }
        return null;
    }

    @Override
    public String getTaskId(String resourceId, ReportMetricEnum[] metricEnum) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(resourceId);
        for (int i = 0; i < metricEnum.length; i++) {
            stringBuffer.append(metricEnum[i]);
        }
        String md5 = SecureUtil.md5(stringBuffer.toString());
        return md5;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            boolean isMaster = deployService.currentNodeIsMaster();
            if (isMaster) {
                this.WarnStrategyPullTask();
                CloudExecutorServices.get().getCommonService().execute(() -> this.pullWarnStrategyFromDataCenter());
                log.info("O.o<WarnStrategyFromDataCenter>o.O pullWarnStrategyFromDataCenter init finish");
            }
        } catch (Exception e) {
            log.error("生成告警策略拉取定时任务失败: " + e);
        }
    }
}
