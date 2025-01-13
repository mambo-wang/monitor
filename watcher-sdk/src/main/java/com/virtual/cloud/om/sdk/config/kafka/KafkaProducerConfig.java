package com.virtual.cloud.om.sdk.config.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 默认的kafka消息生产者bean
 *
 * @author w22798
 */
@EnableConfigurationProperties(KafkaProducerProperties.class)
@Configuration
@ConditionalOnMissingBean(value = KafkaProducer.class)
public class KafkaProducerConfig {

    @Autowired
    private KafkaProducerProperties kafkaProducerProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", kafkaProducerProperties.getKeySerializer());
        props.put("value.serializer", kafkaProducerProperties.getValueSerializer());
        props.put("client.id", kafkaProducerProperties.getClientId());
        // 0：这意味着生产者producer不等待来自broker同步完成的确认继续发送下一条（批）消息。此选项提供最低的延迟但最弱的耐久性保证（当服务器发生故障时某些数据会丢失，如leader已死，但producer并不知情，发出去的信息broker就收不到）。
        // 1：这意味着producer在leader已成功收到的数据并得到确认后发送下一条message。此选项提供了更好的耐久性为客户等待服务器确认请求成功（被写入死亡leader但尚未复制将失去了唯一的消息）。
        // -1：这意味着producer在follower副本确认接收到数据后才算一次发送完成。
        // 此选项提供最好的耐久性，我们保证没有信息将丢失，只要至少一个同步副本保持存活。
        props.put("acks", kafkaProducerProperties.getAcks());
//        props.put("compression.type", kafkaProducerProperties.getCompressionType());//报错说不支持那种压缩方式
        props.put("batch.size", kafkaProducerProperties.getBatchSize());
//        props.put("buffer.memory", kafkaProducerProperties.getBufferMemory());
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }

}
