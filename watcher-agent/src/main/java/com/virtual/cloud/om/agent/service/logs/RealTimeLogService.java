package com.virtual.cloud.om.agent.service.logs;

import com.virtual.cloud.om.agent.entity.LogMetaData;
import com.virtual.cloud.om.agent.entity.RealTimeLogStrategy;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.*;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.FuncUtil;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实时日志服务 - MySQL 单机版
 * 移除了 Kafka、Elasticsearch 依赖
 * @Author: w22798
 * @Date: 2022/5/4 14:51
 */
@Slf4j
@Service("realTimeLogApi")
public class RealTimeLogService implements RealTimeLogApi, ApplicationRunner {

    @Resource
    private CasRestConnection casRestConnection;

    @Resource
    private HostApi[] hostApis;

    private DeployApi deployApi;

    private ResourceApi resourceApi;

    @Resource
    private DataCenterApi dataCenterApi;

    @Resource
    private LockApi lockApi;

    @Resource
    private LogPatternApi[] logPatternApis;

    @Autowired
    public void setResourceApi(ResourceApi resourceApi) {
        this.resourceApi = resourceApi;
    }

    private Map<ReportResourceEnum, HostApi> hostApiMap = new ConcurrentHashMap<>();
    private Map<String, LogPatternApi> logPatternApiMap = new ConcurrentHashMap<>();

    @Autowired
    public void setDeployApi(DeployApi deployApi) {
        this.deployApi = deployApi;
    }

    public Map<String, String> logPathTargetType = new ConcurrentHashMap<>();
    public Map<String, String> logPathLogType = new ConcurrentHashMap<>();

    private Optional<RealTimeLogStrategy> convert(RealTimeLogStrategyRequest request) {
        String tags = request.getTags();
        String[] tagArr = tags.split(";");
        String resourceId = StringUtils.substringAfter(tagArr[0], "resourceId=");
        Set<String> allNodeIds = new LinkedHashSet<>();
        if (!request.getPlatform().equals(ReportResourceEnum.onestor.name())) {
            allNodeIds.add("0");
        }

        Set<String> hostIds;
        if (tagArr.length == 1) {
            hostIds = getHostApi(request.getPlatform()).queryHostIds(resourceApi.findRestHostByResourceId(resourceId));
        } else {
            String hostIdsStr = StringUtils.substringAfter(tagArr[1], "hostIds=");
            hostIds = Stream.of(hostIdsStr.split(",")).collect(Collectors.toSet());
        }

        Set<String> logPaths = request.getLogs().stream()
                .filter(s -> StringUtils.isNotEmpty(s.getTargetType()))
                .map(RealTimeLogStrategyLogs::getLogPath)
                .collect(Collectors.toSet());

        if (logPaths.isEmpty()) {
            return Optional.empty();
        }

        allNodeIds.addAll(hostIds);

        RealTimeLogStrategy realTimeLogStrategy = RealTimeLogStrategy.builder()
                .logPaths(logPaths)
                .platform(request.getPlatform())
                .resourceId(resourceId)
                .targets(allNodeIds)
                .build();

        return Optional.of(realTimeLogStrategy);
    }

    @Override
    public void handleIncreasedRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request) {
        log.warn("[RealTimeLogService] 实时日志策略处理已禁用（Kafka已移除）");
    }

    @Override
    public void handleRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request) {
        log.warn("[RealTimeLogService] 实时日志策略处理已禁用（Kafka已移除）");
    }

    private boolean handleRealTimeLog(RealTimeLogStrategy strategy, String operate) {
        log.warn("[RealTimeLogService] 实时日志处理已禁用（Kafka已移除）");
        return false;
    }

    private Set<String> handleRealTimeLog(RealTimeLogStrategy strategy, Set<String> targets, String operate) {
        return Collections.emptySet();
    }

    private boolean handleRealTimeLog(RealTimeLogStrategy strategy, String target, String operate) {
        log.warn("[RealTimeLogService] 实时日志处理已禁用（Kafka已移除）");
        return false;
    }

    public void deployFileBeat(SSHHost sshHost, RealTimeLogStrategy realTimeLogStrategy, Set<String> kafkaServerHost) {
        log.warn("[RealTimeLogService] FileBeat部署已禁用（Kafka已移除）");
    }

    @SneakyThrows
    private void validFileBeat(SSHHost sshHost) {
        log.warn("[RealTimeLogService] FileBeat验证已禁用（Kafka已移除）");
    }

    @SneakyThrows
    private void copyFileBeat(SSHHost sshHost) {
        log.warn("[RealTimeLogService] FileBeat拷贝已禁用（Kafka已移除）");
    }

    private String getFileBeatYmlPath() {
        return "";
    }

    private String getFileBeatPath() {
        return "";
    }

    private String getThreadFileBeatPath() {
        return "";
    }

    private void duplicateThreadFilebeat() {
    }

    private void deleteThreadFilebeat() {
    }

    @SneakyThrows
    private void modifyFileBeatYml(SSHHost sshHost, RealTimeLogStrategy realTimeLogStrategy, Set<String> kafkaServerHost) {
        log.warn("[RealTimeLogService] FileBeat配置已禁用（Kafka已移除）");
    }

    private Object getTags(SSHHost sshHost) {
        return "";
    }

    @SneakyThrows
    @Override
    public void realtimeLogUpload(SSHHost sshHost, String operate) {
        log.warn("[RealTimeLogService] 实时日志上传已禁用（Kafka已移除）");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stream.of(hostApis).forEach(collectApi -> {
            ReportResourceEnum resource = collectApi.whoAreYou();
            hostApiMap.put(resource, collectApi);
        });

        Stream.of(logPatternApis).forEach(collectApi -> {
            RealTimeLogTypeEnum logType = collectApi.logType();
            logPatternApiMap.put(logType.name(), collectApi);
        });
    }

    private HostApi getHostApi(String platform) {
        return Optional.ofNullable(hostApiMap.get(ReportResourceEnum.valueOf(platform)))
                .orElseThrow(() -> new AppException(ErrorCodes.NOT_FOUND));
    }

    private void refreshLogMetaData(Set<LogMetaData> logMetaData) {
        log.info("[RealTimeLogService] 日志元数据刷新已禁用（MongoDB已移除）");
    }

    private LogPatternApi getLogPatternApi(String logType) {
        try {
            if (StringUtils.isEmpty(logType)) {
                return logPatternApiMap.get(RealTimeLogTypeEnum.others_.name());
            }
            return Optional.ofNullable(logPatternApiMap.get(logType))
                    .orElse(logPatternApiMap.get(RealTimeLogTypeEnum.others_.name()));
        } catch (Exception e) {
            return logPatternApiMap.get(RealTimeLogTypeEnum.others_.name());
        }
    }

    @Override
    public Optional<LogLine> parseLine(String logType, String message) {
        LogPatternApi logPatternApi = getLogPatternApi(logType);
        if (Objects.isNull(logPatternApi)) {
            LogLine logLine = new LogLine();
            logLine.setMessage(message);
            return Optional.of(logLine);
        }
        return logPatternApi.parseLine(message);
    }

    @Override
    public Map<String, String> queryLogPathTargetType() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> queryLogPathLogType() {
        return Collections.emptyMap();
    }

    @Override
    public void createAndConsumeFilebeatLogTopic(int numPartitions, int replicationFactor) {
        log.warn("[RealTimeLogService] Kafka Topic创建已禁用");
    }

    @Override
    public void handleFilebeatCheck() {
        log.info("[filebeat-check] FileBeat检查已禁用（Kafka已移除）");
    }

    private void handleFilebeatCheck(RealTimeLogStrategy strategy) {
        log.warn("[RealTimeLogService] FileBeat检查已禁用（Kafka已移除）");
    }

    @Override
    public void deleteResourceRealTimeLog(List<String> resourceIds) {
        log.warn("[RealTimeLogService] 删除实时日志已禁用（Kafka已移除）");
    }

    @Override
    public void clearCache() {
        logPathTargetType.clear();
        logPathLogType.clear();
    }

    @Override
    public List<LogLine> searchAll(String platform, Long resourceId, String type, Long targetId, 
            String path, String queryString, Long startTime, Long endTime, 
            Integer sortDir, String sortField, Integer logNum, String level) {
        log.warn("[RealTimeLogService] 日志搜索已禁用（Elasticsearch已移除）");
        return Collections.emptyList();
    }

    private String generateQueryString(String watcherCode, String platform, Long resourceId, String type, 
            Long targetId, String path, String queryString, Long startTime, Long endTime) {
        return "";
    }
}
