package com.virtual.cloud.om.sdk.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.kafka.consumer")
public class KafkaConsumerProperties {

    private String groupId;

    private String autoOffsetReset;

    private String enableAutoCommit;

    private String autoCommitInterval;

    private String keyDeserializer;

    private String valueDeserializer;

    private int maxPollRecords;

    private String fetchMinSize;

    private String heartbeatInterval;

    private String sessionTimeoutMs;

    private String maxPollInterval;
}
