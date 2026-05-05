package com.virtual.cloud.om.agent.service.logs;

import com.virtual.cloud.om.agent.entity.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志服务 - MySQL 单机版
 * MongoDB 版本已禁用，使用简化实现
 *
 * @Author: w22798
 * @Date: 2022/4/26 16:43
 */
@Service("operationLogApi")
@Slf4j
public class LogService {

    /**
     * 保存操作日志 - 已禁用
     */
    public void saveLog(Object operationLog) {
        log.debug("[Log] 操作日志保存功能已禁用");
    }

    /**
     * 查询日志 - 已禁用
     */
    public List<OperationLog> findLogs(OperationLog operationLog) {
        log.debug("[Log] 日志查询功能已禁用");
        return new ArrayList<>();
    }

    /**
     * 删除日志 - 已禁用
     */
    public long deleteLogBeforeTime(String time, boolean persistent) {
        log.debug("[Log] 日志删除功能已禁用");
        return 0;
    }

    /**
     * 保存 Kafka 消费者偏移量日志 - 已禁用
     */
    public void saveOrUpdateKafkaConsumerOffsetLog(String topic, Integer partition, String groupId, Long offset, Exception e) {
        log.debug("[Kafka] Kafka 消费者偏移量日志功能已禁用");
    }
}
