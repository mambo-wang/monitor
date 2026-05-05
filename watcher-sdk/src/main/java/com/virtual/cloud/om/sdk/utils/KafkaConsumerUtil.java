package com.virtual.cloud.om.sdk.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Kafka 消费者工具类 - 已禁用
 * MySQL 单机版不使用 Kafka
 */
@Component
@ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true", matchIfMissing = false)
public class KafkaConsumerUtil {
    // Kafka 消费者工具类已禁用
}
