package com.virtual.cloud.om.agent.service.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virtual.cloud.om.agent.dto.MandatoryDTO;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportErrorTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.ReportErrorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import com.virtual.cloud.om.sdk.entity.mysql.ResourceEntity;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.mapper.ResourceEntityMapper;
import com.virtual.cloud.om.agent.service.strategy.StrategyService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据上报服务 - MySQL 单机版
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataReportService {
    private DataCenterApi dataCenterApi;
    private ResourceEntityMapper resourceEntityMapper;
    private ResourceApi resourceApi;
    private final LockApi lockApi;
    private final TaskRepository taskRepository;
    private final TaskMgrApi taskMgrApi;
    private StrategyService strategyService;
    private final DataReportCollector[] dataReportCollectors;
    private Map<String, DataReportCollector> collectApiMap = Maps.newConcurrentMap();

    @Autowired
    public void setStrategyService(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @Autowired
    public void setDataCenterApi(DataCenterApi dataCenterApi) {
        this.dataCenterApi = dataCenterApi;
    }

    @Autowired
    public void setResourceEntityMapper(ResourceEntityMapper resourceEntityMapper) {
        this.resourceEntityMapper = resourceEntityMapper;
    }

    @PostConstruct
    public void init() {
        Stream.of(dataReportCollectors).forEach(collectApi -> collectApiMap.put(collectApi.metric().name(), collectApi));
    }

    @Autowired
    public void setResourceApi(ResourceApi resourceApi) {
        this.resourceApi = resourceApi;
    }

    /**
     * 强制上报静态数据
     */
    public void mandatory(String data) {
        MandatoryDTO mandatoryDTO = JSONUtil.toBean(data, MandatoryDTO.class);
        log.info("[data collect][static={}][resourceIds={}] static data report[mandatory] is running", true, mandatoryDTO.getResourceIds());
        String metrics = ReportMetricEnum.getStaticMetrics().stream().map(ReportMetricEnum::name).collect(Collectors.joining(";"));
        Arrays.stream(mandatoryDTO.getResourceIds().split(",")).parallel()
                .forEach(resourceId -> {
                    this.report(new StringBuilder("resourceId=").append(resourceId).toString(), metrics);
                    {
                        ReportMetricEnum.getStaticMetrics().stream().forEach(s -> {
                            String taskId = this.strategyService.getTaskId(resourceId, s);
                            Task task = this.taskRepository.findById(taskId);
                            if (Objects.isNull(task) || StrUtil.isBlank(task.getId())) {
                                return;
                            }
                            TaskDTO dto = BeanUtil.copyProperties(task, TaskDTO.class);
                            this.taskMgrApi.delete(dto);
                            this.taskMgrApi.addTask(dto);
                        });
                    }
                });
        log.info("[data collect][static={}][resourceIds={}] static data report[mandatory] is end", true, mandatoryDTO.getResourceIds());
    }

    /**
     * 数据上报
     */
    public void report(String tags, String metrics) {
        Optional<String> first = DataReportCollector.getId("resourceId", tags).stream().findFirst();
        if (!first.isPresent()) {
            throw new AppException(ErrorCodes.RESTHOST_RESOURCEID_NONE);
        }
        final String resourceId = first.get();
        String batchNum = String.valueOf(System.currentTimeMillis() / 1000 / 60);
        ReportMetricEnum[] metricArr = Arrays.stream(metrics.split(";")).map(metric -> ReportMetricEnum.valueOf(metric)).toArray(ReportMetricEnum[]::new);
        if (Arrays.stream(metricArr).filter(metric -> metric.staticMetric).findFirst().isPresent()) {
            String lockKey = String.format(Constant.LockKey.STATIC_DATA_REPORT, resourceId);
            String acquire = this.lockApi.acquire(lockKey, TimeUnit.MINUTES.toMillis(30));
            if (StrUtil.isBlank(acquire)) {
                log.info("[data collect][static={}][resourceId={}][batchNum={}] static data report task is running, no need mandatory", true, resourceId, batchNum);
                return;
            }
            try {
                this.report(resourceId, tags, batchNum, true, metricArr);
            } finally {
                this.lockApi.release(lockKey, acquire);
                // 记录最后一次上报时间 - MySQL版本暂时禁用
                log.debug("[data collect] last report time recording disabled in MySQL standalone mode");
            }
        } else {
            this.report(resourceId, tags, batchNum, false, metricArr);
        }
    }

    /**
     * 数据上报
     */
    private void report(String resourceId, String tags, String batchNum, Boolean ifStatic, ReportMetricEnum... metrics) {
        ReportResourceEnum platform;
        RestHost restHost;
        boolean reportWatcher = resourceId.equals("watcher");
        if (reportWatcher) {
            platform = ReportResourceEnum.hccAgent;
            restHost = RestHost.builder().platform(platform.name()).build();
        } else {
            restHost = this.resourceApi.findRestHostByResourceId(resourceId);
            platform = ReportResourceEnum.valueOf(restHost.getPlatform());
            if (Objects.isNull(platform)) {
                log.info("[data collect][resourceId={}][batchNum={}][static={}] platform is null ", resourceId, batchNum, ifStatic);
                return;
            }
        }
        final RestHost rh = restHost;
        List<ReportError> reportErrorList = Lists.newCopyOnWriteArrayList();
        CompletableFuture[] completableFutures = Arrays.stream(metrics).map(metric ->
                CompletableFuture.runAsync(() -> {
                    final List<DataReportTypeByMetricEnum> types = DataReportTypeByMetricEnum.getTypesByMetricAndPlatform(metric, platform);
                    final String traceId = UUID.fastUUID().toString();
                    List<ReportDTO> data = Lists.newCopyOnWriteArrayList();
                    CompletableFuture[] innerCompletableFutures = types.stream().map(type ->
                            CompletableFuture.runAsync(() -> {
                                log.info("[data collect] type={} [resourceId={}] [{}]", type, resourceId, metric);
                                DataReportCollector collector = collectApiMap.get(type.name());
                                if (Objects.isNull(collector)) {
                                    if (Objects.isNull(collector = collectApiMap.get(type.metric.name()))) {
                                        log.info("[data collect][resourceId={}][batchNum={}][static={}][{}][traceId={}] cant find collector", resourceId, batchNum, ifStatic, metric, traceId);
                                        return;
                                    }
                                }
                                try {
                                    List<ReportDTO> dataSplit = collector.data(rh, tags);
                                    dataSplit.forEach(r -> r.setBatchNum(batchNum));
                                    data.addAll(dataSplit);
                                } catch (Exception e) {
                                    if (e instanceof AppException) {
                                        if (!reportWatcher) {
                                            reportErrorList.add(new ReportError((AppException) e, metric, traceId));
                                        }
                                    }
                                    e.printStackTrace();
                                    log.error("[data collect][resourceId={}][batchNum={}][static={}][{}][traceId={}] collector error:{}", resourceId, batchNum, ifStatic, metric, traceId, e.getMessage());
                                }
                            })
                    ).toArray(CompletableFuture[]::new);
                    CompletableFuture.allOf(innerCompletableFutures).join();
                    if (CollUtil.isEmpty(data)) {
                        return;
                    }
                    try {
                        for (ReportDTO da : data) {
                            log.info("save data {}", da);
                        }
                    } catch (AppException e) {
                        log.error("collect error", e);
                    }
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
    }

    @Data
    @AllArgsConstructor
    private class ReportError {
        private AppException e;
        private ReportMetricEnum metric;
        private String traceId;
    }

    /**
     * 上报异常
     */
    public void reportError(Integer datacenterType, String cloudToken, Integer errorCode, String errorMessage, String tags, ReportErrorTypeEnum type, String resourceId, ReportMetricEnum metric,
                           String watcherCode, byte[] secretKey, String host, String port, String token, String traceId,
                           String comCode, String orgCode) {
        try {
            ResourceEntity resource = resourceEntityMapper.selectById(resourceId);
            if (Objects.isNull(resource)) {
                throw new AppException(ErrorCodes.RESTHOST_RESOURCE_NONE);
            }
            ReportErrorDTO dto = new ReportErrorDTO();
            dto.setErrorMessage(errorMessage);
            dto.setTags(tags);
            dto.setReportTimestamp(System.currentTimeMillis());
            dto.setTraceId(UUID.fastUUID().toString());
            dto.setWatcherCode(watcherCode);
            dto.setOrgCode(orgCode);
            dto.setComCode(comCode);
            dto.setErrorCode(errorCode);
            dto.setType(type);
            dto.setPlatform(resource.getPlatform());
            log.info("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info ：{} ", resourceId, metric, host, traceId, errorMessage);
        } catch (Exception e) {
            log.error("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info fail :{} ", resourceId, metric, host, traceId, e.getMessage());
        }
    }
}
