package com.virtual.cloud.om.sdk.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.kafka.producer")
public class KafkaProducerProperties {

    private String clientId;

    private String retries;

    private String batchSize;

    private String bufferMemory;

    private String acks;

    private String keySerializer;

    private String valueSerializer;

    private String compressionType;
}
