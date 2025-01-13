package com.virtual.cloud.om.log.service.kafka;

import com.virtual.cloud.om.log.service.LogService;
import com.virtual.cloud.om.sdk.utils.SpringContextsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import java.util.Map;
import java.util.Optional;

/**
 * @author w22798
 * @version v1.0
 * @ClassName GeneralOffsetCommitCallback
 * @Description 通用的消费者偏移量提交回调处理器
 */
@Slf4j
public class GeneralOffsetCommitCallback implements OffsetCommitCallback {

    private String groupId;

    private static LogService logService = SpringContextsUtil.getBean(LogService.class);

    public GeneralOffsetCommitCallback(String groupId){
        this.groupId =groupId;
    }

    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
        Optional.ofNullable(map).ifPresent(itt -> {
            itt.forEach((partition, offset) -> logService.saveOrUpdateKafkaConsumerOffsetLog(partition.topic(), partition.partition(), groupId, offset.offset(), e));
        });

    }
}
