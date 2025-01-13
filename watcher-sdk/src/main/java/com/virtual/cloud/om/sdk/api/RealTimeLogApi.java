package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.LogLine;
import com.virtual.cloud.om.sdk.dto.RealTimeLogStrategyRequest;
import com.virtual.cloud.om.sdk.dto.SSHHost;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: w22798
 * @Date: 2022/5/4 14:41
 */
public interface RealTimeLogApi {


    /**
     * 接收websocket推送消息以及定时查询接口返回值，处理日志实时上报逻辑
     * todo 考虑分离部署情况下cas接口查不到cvm主机的问题
     * @param request
     */
    void handleRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request);

    /**
     * 针对新增资源时仅下发新增的资源的日志实时采集策略，处理日志实时上报逻辑
     * @param request
     */
    void handleIncreasedRealTimeLogStrategy(List<RealTimeLogStrategyRequest> request);
    /**
     * 开启/关闭日志实时收集
     * @param sshHost 主机信息
     * @param operate 操作类型: startup shutdown
     */
    void realtimeLogUpload(SSHHost sshHost, String operate);

    /**
     * 解析日志行
     * @param logType 日志类型
     * @param message 日志内容
     * @return 日志行
     */
    Optional<LogLine> parseLine(String logType, String message);

    Map<String, String> queryLogPathTargetType();

    Map<String, String> queryLogPathLogType();

    void createAndConsumeFilebeatLogTopic(int numPartitions, int replicationFactor);

    void handleFilebeatCheck();

    void deleteResourceRealTimeLog(List<String> resourceIds);

    void clearCache();

    List<LogLine> searchAll(String platform, Long resourceId, String type, Long targetId, String path, String queryString, Long startTime, Long endTime, Integer sortDir, String sortField, Integer logNum, String level);
}
