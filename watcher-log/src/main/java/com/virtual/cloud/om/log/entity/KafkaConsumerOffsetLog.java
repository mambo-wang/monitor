package com.virtual.cloud.om.log.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * kafka消费者消费偏移量
 */
@Data
@Document(collection = "KafkaConsumerOffsetLog")
public class KafkaConsumerOffsetLog{
    /**
     * 主键,topic_partition_groupId
     */
    @Id
    private String id;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息分区
     */
    private Integer partition;
    /**
     * 消费组
     */
    private String  groupId;
    /**
     * 消息偏移量
     */
    private Long  offset;
    /**
     * 创建时间
     */
    private Date time;

    private Exception exception;

}
