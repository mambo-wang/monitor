package com.virtual.cloud.om.agent.service.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virtual.cloud.om.agent.dto.MandatoryDTO;
import com.virtual.cloud.om.agent.entity.LastReportStaticDataTime;
import com.virtual.cloud.om.agent.entity.ResourceEntity;
import com.virtual.cloud.om.agent.entity.Task;
import com.virtual.cloud.om.agent.repository.TaskRepository;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportErrorTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.constant.uri.DataCenterUriConstants;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.ReportErrorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import com.virtual.cloud.om.sdk.entity.clickhouse.AwesomeMetric;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.agent.service.strategy.StrategyService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataReportService {
    private DataCenterApi dataCenterApi;
    private final MongoTemplate mongoTemplate;
    private  ResourceApi resourceApi;
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
     *
     * @param data
     */
    public void mandatory(String data) {
        MandatoryDTO mandatoryDTO = JSONUtil.toBean(data, MandatoryDTO.class);
        log.info("[data collect][static={}][resourceIds={}] static data report[mandatory] is running", true, mandatoryDTO.getResourceIds());
        String metrics = ReportMetricEnum.getStaticMetrics().stream().map(ReportMetricEnum::name).collect(Collectors.joining(";"));
        Arrays.stream(mandatoryDTO.getResourceIds().split(",")).parallel()
                .forEach(resourceId -> {
                    this.report(new StringBuilder("resourceId=").append(resourceId).toString(), metrics);
                    //修改定时任务
                    {
                        ReportMetricEnum.getStaticMetrics().stream().forEach(s->{
                            String taskId = this.strategyService.getTaskId(resourceId, s);
                            Task task = this.taskRepository.findById(taskId);
                            if (Objects.isNull(task) || StrUtil.isBlank(task.getId())) {
                                return;
                            }
                            TaskDTO dto = BeanUtil.copyProperties(task, TaskDTO.class);
                            this.taskMgrApi.delete(dto);
                            this.taskMgrApi.addTask(dto);
                        });
//                        String taskId = this.strategyService.getTaskId(resourceId, ReportMetricEnum.getStaticMetrics().stream().toArray(ReportMetricEnum[]::new));
                    }
                });
        log.info("[data collect][static={}][resourceIds={}] static data report[mandatory] is end", true, mandatoryDTO.getResourceIds());
    }

    /**
     * 数据上报
     *
     * @param tags
     * @param metrics
     */
    public void report(String tags, String metrics) {
        // tags 中拆 resourceId
        Optional<String> first = DataReportCollector.getId("resourceId", tags).stream().findFirst();
        if (!first.isPresent()) {
            throw new AppException(ErrorCodes.RESTHOST_RESOURCEID_NONE);
        }
        final String resourceId = first.get();
        // 批次号
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
                // 记录最后一次上报时间
                {
                    Query query = Query.query(Criteria.where("resourceId").is(resourceId));
                    Update update = new Update()
                            // 不存在就新增，存在无操作
                            .setOnInsert("_id", resourceId)
                            // 不存在就新增，存在就更新
                            .set("lastTimeMs", System.currentTimeMillis());
                    this.mongoTemplate.upsert(query, update, LastReportStaticDataTime.class);
                }
            }
        } else {
            this.report(resourceId, tags, batchNum, false, metricArr);
        }
    }

    /**
     * 数据上报
     *
     * @param tags
     * @param metrics
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
                    final List<DataReportTypeByMetricEnum> types =DataReportTypeByMetricEnum.getTypesByMetricAndPlatform(metric, platform);
                    final String traceId = UUID.fastUUID().toString();
                    List<ReportDTO> data = Lists.newCopyOnWriteArrayList();
                    CompletableFuture[] innerCompletableFutures = types.stream().map(type ->
                            CompletableFuture.runAsync(() -> {
                                log.info("[data collect] type={} [resourceId={}] [{}]",type,resourceId,metric);
                                DataReportCollector collector = collectApiMap.get(type.name());
                                if (Objects.isNull(collector)) {
                                    if (Objects.isNull(collector= collectApiMap.get(type.metric.name()))){
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
                                    log.error("[data collect][resourceId={}][batchNum={}][static={}][{}][traceId={}] collector error:{}", resourceId, batchNum, ifStatic, metric, traceId,e.getMessage());
                                }
                            })
                    ).toArray(CompletableFuture[]::new);
                    CompletableFuture.allOf(innerCompletableFutures).join();
                    if(CollUtil.isEmpty(data)){
                        return;
                    }
                    try {
                        for (ReportDTO da : data) {
                            log.info("save data {}", da);
                            //暂时先处理数字格式的指标
                            if(da.getType() == ReportDataTypeEnum.gauge){
                                AwesomeMetric awesomeMetric = new AwesomeMetric();
                                awesomeMetric.setMetric(da.getMetric().toString());
                                awesomeMetric.setBatchNum(batchNum);
                                awesomeMetric.setPlatform(platform.toString());
                                awesomeMetric.setTraceId(traceId);
                                awesomeMetric.setCreateTime(da.getTimestamp());
                                awesomeMetric.setTags(da.getTags());
                                String value = String.valueOf(da.getValue());
                                awesomeMetric.setValue(StringUtils.isNumeric(value) ? Double.valueOf(value) : 0.00);
                                //写入时序数据库
                            }
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
     *
     * @param errorCode
     * @param errorMessage
     * @param tags
     * @param type
     */
    public void reportError(Integer datacenterType,String cloudToken,Integer errorCode, String errorMessage, String tags, ReportErrorTypeEnum type, String resourceId, ReportMetricEnum metric,
                            String watcherCode, byte[] secretKey, String host,String port, String token, String traceId,
                            String comCode, String orgCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(resourceId));
        try {
            ResourceEntity resource = this.mongoTemplate.findOne(query, ResourceEntity.class);
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
            String uri = DataCenterUriConstants.REPORT_ERROR;
            try {
//todo wb
                log.info("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info ：{} ", resourceId, metric, host, traceId, errorMessage);
            } catch (AppException e) {
                // 数据中心返回了这个code，需要重新上报一次
                if (e.getErrorCode().equals(ErrorCodes.report_data_error_need_report_again)) {
                    try {
//todo wb
                        log.info("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info ：{} ", resourceId, metric, host, traceId, errorMessage);
                    } catch (Exception e1) {
                        log.error("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info fail :{} ", resourceId, metric, host, traceId, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("[data collect][resourceId={}][{}][data center host={}][traceId={}] report error info fail :{} ", resourceId, metric, host, traceId, e.getMessage());
        }
    }
}
