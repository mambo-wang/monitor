package com.virtual.cloud.om.sdk.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Properties;

/**
 * kafka消费者实例构造器
 * @Author: w22798
 * @Date: 2022/5/14 14:01
 */
public class KafkaConsumerBuilder {

    public static KafkaConsumer<String, String> kafkaConsumer(String bootstrapServers, KafkaConsumerProperties kafkaConsumerProperties, String groupId) {
        Properties props = new Properties();
        props.put("group.id", groupId);
        props.put("bootstrap.servers", bootstrapServers);
        props.put("enable.auto.commit", kafkaConsumerProperties.getEnableAutoCommit());// 自动提交
        props.put("auto.commit.interval.ms", kafkaConsumerProperties.getAutoCommitInterval());
        props.put("auto.offset.reset", kafkaConsumerProperties.getAutoOffsetReset());// in("latest","earliest","none"),
        props.put("key.deserializer", kafkaConsumerProperties.getKeyDeserializer());
        props.put("value.deserializer", kafkaConsumerProperties.getValueDeserializer());
        props.put("max.poll.records", kafkaConsumerProperties.getMaxPollRecords());
        props.put("fetch.min.size", kafkaConsumerProperties.getFetchMinSize());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerProperties.getHeartbeatInterval());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerProperties.getSessionTimeoutMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerProperties.getMaxPollInterval());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
}
