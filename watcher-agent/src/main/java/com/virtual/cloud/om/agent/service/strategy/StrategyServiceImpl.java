package com.virtual.cloud.om.agent.service.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.agent.dto.StrategyDTO;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.ReportSeparateEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.agent.service.deploy.DeployService;
import com.virtual.cloud.om.agent.service.report.DataReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/5/12 11:05
 */
@Service
@Slf4j
public class StrategyServiceImpl implements StrategyService, ApplicationRunner {

    public final Integer frequency = 30;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private TaskMgrApi taskMgrApi;

    private DataCenterApi DataCenterApi;

    private DeployService deployService;

    private DataReportService dataReportService;
    @Autowired
    public void setDataReportService(DataReportService dataReportService) {
        this.dataReportService = dataReportService;
    }
    @Autowired
    public void setDeployService(DeployService deployService) {
        this.deployService = deployService;
    }
    @Autowired
    public void setDataCenterApi(DataCenterApi dataCenterApi) {
        this.DataCenterApi = dataCenterApi;
    }
    /**
     * 定时策略拉取
     */
    public void StrategyPullTask() {
        TaskDTO task = new TaskDTO();
        task.setId("strategy_pull_task")
                .setTaskName("strategy_pull_task")
                .setAvailableStartTime(System.currentTimeMillis())
                .setCycleType(Task.CYCLE_TYPE_MINUTES)
                .setCycleDay(frequency)
                .setTaskType(Task.TASK_STRATEGY_PULL)
                .setDescription("策略拉取")
                .setCreatedTime(Utils.formatFullDateTime(System.currentTimeMillis()));
        taskMgrApi.addTask(task);
    }

    public void taskserverForPull(List<StrategyDTO> strategyDTOS) {
        log.info("O.o<StrategyFromDataCenter>o.O 策略: {}", Objects.nonNull(strategyDTOS) ?
                JSONUtil.toJsonStr(strategyDTOS.stream().map(StrategyDTO::getMetric).collect(Collectors.toList()))
                : null);
        if (CollectionUtil.isEmpty(strategyDTOS)) {
            taskMgrApi.deleteAllStrategyTask(Task.TASK_STRATEGY_ISSUE);
            log.info("O.o<StrategyFromDataCenter>o.O strategy is empty");
            return;
        }
        List<StrategyDTO> onceStrategys = strategyDTOS.stream().filter(s -> Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        List<StrategyDTO> scheduleStrategiesFromDataCenter = strategyDTOS.stream().filter(s -> !Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        this.onceTask(onceStrategys);
        List<TaskDTO> tasksFromDatabase = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_STRATEGY_ISSUE);
        if (CollectionUtil.isEmpty(tasksFromDatabase)) {
            this.taskGroup(scheduleStrategiesFromDataCenter);
            log.info("O.o<StrategyFromDataCenter>o.O first insert scheduleStrategiesFromDataCenter :" + scheduleStrategiesFromDataCenter);
            return;
        }
        Map<String, List<StrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
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
                List<StrategyDTO> strategyDTOListCenter = resourceIdStrategyMap.get(r);
                if (CollectionUtil.isNotEmpty(taskDTOList)) {
                    Map<String, StrategyDTO> scheduleStrategiesMapFromDataCenter = strategyDTOListCenter.stream().collect(Collectors.toMap(StrategyDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    Map<String, TaskDTO> taskMapFromDatabase = taskDTOList.stream().collect(Collectors.toMap(TaskDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    this.deleteTask(taskDTOList, scheduleStrategiesMapFromDataCenter);
                    this.insertTaskCompare(strategyDTOListCenter, taskMapFromDatabase);
                } else {
                    log.info("O.o<StrategyFromDataCenter>o.O new insert resouceId:{} to task ", r);
                    this.taskGroup(strategyDTOListCenter);
                }
            } catch (Exception e) {
                log.error("O.o<StrategyFromDataCenter>o.O fail to compare resouceId= " + r);
            }
        });

    }

    @Override
    public void taskserver(List<StrategyDTO> strategyDTOS) {
        log.info("O.o<StrategyFromDataCenter>o.O 策略: " + strategyDTOS.size());
        if (CollectionUtil.isEmpty(strategyDTOS)) {
            taskMgrApi.deleteAllStrategyTask(Task.TASK_STRATEGY_ISSUE);
            log.info("O.o<StrategyFromDataCenter>o.O strategy is empty");
            return;
        }
        List<StrategyDTO> onceStrategys = strategyDTOS.stream().filter(s -> Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        List<StrategyDTO> scheduleStrategiesFromDataCenter = strategyDTOS.stream().filter(s -> !Objects.equals(s.getUnit(), Task.CYCLE_TYPE_ONCE)).collect(Collectors.toList());
        this.onceTask(onceStrategys);
        List<TaskDTO> tasksFromDatabase = taskRepository.findByhashNotNullAndTaskTypeIsStrategyIssue(Task.TASK_STRATEGY_ISSUE);
        if (CollectionUtil.isEmpty(tasksFromDatabase)) {
            this.taskGroup(scheduleStrategiesFromDataCenter);
            log.info("O.o<StrategyFromDataCenter>o.O first insert scheduleStrategiesFromDataCenter :" + scheduleStrategiesFromDataCenter);
            return;
        }
        Map<String, List<StrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
        Map<String, List<TaskDTO>> resourceIdTaskMap = tasksFromDatabase.parallelStream().collect(Collectors.groupingBy(taskDTO -> taskDTO.getResourceId()));
        Set<String> resouceIdFromCenter = resourceIdStrategyMap.keySet();
        resouceIdFromCenter.stream().forEach(r -> {
            try {
                List<TaskDTO> taskDTOList = resourceIdTaskMap.get(r);
                List<StrategyDTO> strategyDTOListCenter = resourceIdStrategyMap.get(r);
                if (CollectionUtil.isNotEmpty(taskDTOList)) {
                    Map<String, StrategyDTO> scheduleStrategiesMapFromDataCenter = strategyDTOListCenter.stream().collect(Collectors.toMap(StrategyDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    Map<String, TaskDTO> taskMapFromDatabase = taskDTOList.stream().collect(Collectors.toMap(TaskDTO::getHash, Function.identity(), (key1, key2) -> key2));
                    this.deleteTask(taskDTOList, scheduleStrategiesMapFromDataCenter);
                    this.insertTaskCompare(strategyDTOListCenter, taskMapFromDatabase);
                } else {
                    log.info("O.o<StrategyFromDataCenter>o.O new insert resouceId:{} to task ", r);
                    this.taskGroup(strategyDTOListCenter);
                }
            } catch (Exception e) {
                log.error("O.o<StrategyFromDataCenter>o.O fail to compare resouceId= " + r);
            }
        });

    }

    /**
     * 执行一次的直接调取接口
     *
     * @param onceStrategys
     */
    private void onceTask(List<StrategyDTO> onceStrategys) {
        onceStrategys.parallelStream().forEach(s -> {
            String metric = s.getMetric();
            try {
                dataReportService.report(s.getTags(), metric);
            } catch (Exception e) {
                log.error("O.o<StrategyFromDataCenter>o.O OnceStrategys report fail : " + s + " : " + e);
            }
        });
    }


    /**
     * 获取需要添加的定时任务
     */
    private void insertTaskCompare(List<StrategyDTO> scheduleStrategiesFromDataCenter, Map<String, TaskDTO> taskMapFromDatabase) {
        //找到需要新增的定时任务
        List<StrategyDTO> insertTaskList = null;
        if (Objects.isNull(taskMapFromDatabase)) {
            insertTaskList = scheduleStrategiesFromDataCenter.stream().filter(c -> StringUtils.isNotEmpty(c.getHash())).collect(Collectors.toList());
        } else {
            insertTaskList = scheduleStrategiesFromDataCenter.stream().filter(s ->
                    !taskMapFromDatabase.containsKey(s.getHash()) && StringUtils.isNotEmpty(s.getHash())
            ).collect(Collectors.toList());
        }
        this.taskGroup(insertTaskList);
    }

    private void insertTask(List<StrategyDTO> scheduleStrategiesFromDataCenter) {
        scheduleStrategiesFromDataCenter.stream().forEach(s -> {
            Optional<String> first = DataReportCollector.getId("resourceId", s.getTags()).stream().findFirst();
            if (first.isPresent()) {
                String resouceId = first.get();
                String tags = s.getTags();
                String taskId=null;
                if (tags.contains(Constant.Tags.HOST_IDS_ALL)){
                    taskId= this.createTaskId(resouceId, s.getMetric(), ReportSeparateEnum.host);
                }else if (tags.contains(Constant.Tags.DOMAIN_IDS_ALL)){
                    taskId= this.createTaskId(resouceId, s.getMetric(),ReportSeparateEnum.domain);
                }else if (tags.contains(Constant.Tags.CLUSTER_IDS_ALL)){
                    taskId= this.createTaskId(resouceId, s.getMetric(),ReportSeparateEnum.cluster);
                }else {
                    taskId= this.createTaskId(resouceId, s.getMetric(),null);
                }
                if (Objects.nonNull(taskId)) {
                    TaskDTO task = new TaskDTO();
                    task.setResourceId(resouceId);
                    task.setHash(s.getHash());
                    task.setId(taskId);
                    task.setTaskName(s.getHash());
                    task.setTaskType(Task.TASK_STRATEGY_ISSUE);
                    task.setCycleType(s.getUnit());
                    task.setCycleDay(s.getFrequency());
                    task.setData(s);
                    task.setCreatedTime(Utils.formatFullDateTime(System.currentTimeMillis()));
                    task.setAvailableStartTime(System.currentTimeMillis());
                    task.setDescription(s.getPlatform() + "采集指标：" + s.getMetric());
                    taskMgrApi.addTask(task);
                    log.info("task: "+task);
                    log.info("O.o<StrategyFromDataCenter>o.O [INSERT_TASK]" + s.getPlatform() + "采集指标：" + s.getMetric() + " 定时任务已添加,tags= {}", s.getTags());
                }

            }
        });
    }

    /**
     * 动静分开处理
     *
     * @param scheduleStrategiesFromDataCenter
     */
    private void taskGroup(List<StrategyDTO> scheduleStrategiesFromDataCenter) {
        //动静态数据分开
        this.onceTask(scheduleStrategiesFromDataCenter);
        //动态数据处理
        log.info("O.o<StrategyFromDataCenter>o.O new insert task : " + scheduleStrategiesFromDataCenter);
        Map<String, List<StrategyDTO>> resourceIdStrategyMap = scheduleStrategiesFromDataCenter.parallelStream().collect(Collectors.groupingBy(strategyDTO -> DataReportCollector.getId("resourceId", strategyDTO.getTags()).get(0)));
        Set<String> resourceIds = resourceIdStrategyMap.keySet();
        resourceIds.stream().forEach(r -> {
            List<StrategyDTO> strategyDTOList = resourceIdStrategyMap.get(r);
            this.insertTask(strategyDTOList);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error("[sleep] 10 s fail");
            }
            log.info("[sleep] 10s success>>>>YANZU<<<<");
        });
    }


    private void deleteTask(List<TaskDTO> tasksFromDatabase, Map<String, StrategyDTO> scheduleStrategiesMapFromDataCenter) {
        List<TaskDTO> deleteTaskList = tasksFromDatabase.stream().filter(t -> !scheduleStrategiesMapFromDataCenter.containsKey(t.getHash())).collect(Collectors.toList());
        log.info("O.o<StrategyFromDataCenter>o.O delete task ：" + deleteTaskList);
        deleteTaskList.stream().forEach(t -> taskMgrApi.delete(t));
    }

    private String createTaskId(String resourceId, String metric,ReportSeparateEnum reportSeparateEnum) {

        String id=null;
        String metricReplace;
        if (Objects.nonNull(resourceId) && Objects.nonNull(metric)) {
            if (Objects.nonNull(reportSeparateEnum)){
                metricReplace= metric.replace(";", "");
                Optional<DataReportTypeByMetricEnum> first = DataReportTypeByMetricEnum.getTypesByMetricAndSeparate(ReportMetricEnum.valueOf(metricReplace), reportSeparateEnum).stream().findFirst();
                if (first.isPresent()){
                    DataReportTypeByMetricEnum dataReportTypeByMetricEnum = first.get();
                    log.info("dataReportTypeByMetricEnum " +dataReportTypeByMetricEnum);
                     id= resourceId  + dataReportTypeByMetricEnum;
                }
            }else {
                metricReplace = metric.replace(";", "");
                id = resourceId + metricReplace;
            }
            String md5 = SecureUtil.md5(id);
            return md5;
        }
        return null;
    }


    @Override
    public String getTaskId(String resourceId, ReportMetricEnum metricEnum) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(resourceId);
//        for (int i = 0; i < metricEnum.length; i++) {
//            stringBuffer.append(metricEnum[i]);
//        }
        stringBuffer.append(metricEnum);
        String md5 = SecureUtil.md5(stringBuffer.toString());
        return md5;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        /**
         * 主节点启动执行一次拉取
         */
        try {
            boolean isMaster = deployService.currentNodeIsMaster();
            log.info("=====策略定时任务========== {}",this);
            if (isMaster) {
                log.info("O.o<StrategyFromDataCenter>o.O add task begin");
                this.StrategyPullTask();
                log.info("O.o<StrategyFromDataCenter>o.O add task success");
                log.info("O.o<StrategyFromDataCenter>o.O pullStrategyFromDataCenter init finish");
            }
        } catch (Exception e) {
            log.error("生成策略拉取定时任务失败: " + e);
        }
    }
}
