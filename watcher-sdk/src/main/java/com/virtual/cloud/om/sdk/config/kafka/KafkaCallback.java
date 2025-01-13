package com.virtual.cloud.om.sdk.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class KafkaCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {

        if(metadata != null){
            log.debug("kafka send msg success！{}", metadata.toString());
        } else {
            log.error("kafka send msg fail, cause {}", exception.toString());
        }
    }
}
