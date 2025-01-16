package com.virtual.cloud.om.sdk.config.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.requests.DescribeLogDirsResponse;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @ClassName ConsoleApi
 * @Author wangbao
 * @Describe 主题操作控制类
 * @Date 2019/5/31 0031 9:32
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true", matchIfMissing = false)
public class KafkaConsole {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapservers;

    private volatile static AdminClient adminClient;

    public AdminClient newAdminClient(){
        if(Objects.isNull(adminClient)){
            synchronized (KafkaConsole.class){
                if(Objects.isNull(adminClient)){
                    Map<String, Object> props = new HashMap<>();
                    //配置Kafka实例的连接地址
                    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapservers);
                    adminClient = AdminClient.create(props);
                }
            }
        }
        return adminClient;
    }

    /**
     * 返回主题的信息
     * @param topicName 主题名称
     * @return
     */
    public Map<String, TopicDescription> selectTopicInfo(String topicName) throws ExecutionException, InterruptedException {
        DescribeTopicsResult result = newAdminClient().describeTopics(Arrays.asList(topicName));
        Map<String, TopicDescription> all = result.all().get();
        return all;
    }

    @SneakyThrows
    public ListTopicsResult queryTopics(){
        ListTopicsResult listTopicsResult = newAdminClient().listTopics();
        return listTopicsResult;
    }


    /**
     * 增加某个主题的分区（注意分区只能增加不能减少）
     * @param topicName  主题名称
     * @param number  修改数量
     */
    @SneakyThrows
    public void increaseTopicPartitions(String topicName, Integer number){
        Map<String, NewPartitions> newPartitions=new HashMap<String, NewPartitions>();
        //创建新的分区的结果
        newPartitions.put(topicName, NewPartitions.increaseTo(number));
        CreatePartitionsResult createPartitionsResult = newAdminClient().createPartitions(newPartitions);
        createPartitionsResult.all().get();
    }

    /**
     * 查询broker占用磁盘空间
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public long getSpace() throws ExecutionException, InterruptedException {

            DescribeLogDirsResult ret = newAdminClient().describeLogDirs(Collections.singletonList(1)); // 指定Broker id，在zookeeper中/brokers/ids路径下
            long size = 0L;
            for (Map<String, DescribeLogDirsResponse.LogDirInfo> logDirInfoMap : ret.all().get().values()) {
                size += logDirInfoMap.values().stream().map(logDirInfo -> logDirInfo.replicaInfos).flatMap(
                        topicPartitionReplicaInfoMap ->
                                topicPartitionReplicaInfoMap.values().stream().map(replicaInfo -> replicaInfo.size))
                        .mapToLong(Long::longValue).sum();
            }
            log.info("[kafka] broker use disk size :{}", size);
            return size;
    }

    /**
     * 查询某个消费者组的位移
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public Map<TopicPartition, OffsetAndMetadata> queryOffsets() throws InterruptedException, ExecutionException, TimeoutException {
        ListConsumerGroupOffsetsResult result = newAdminClient().listConsumerGroupOffsets(groupId);
        Map<TopicPartition, OffsetAndMetadata> offsets =
                result.partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);
        return offsets;
    }

    @SneakyThrows
    public void createTopic(String topicName, int numPartitions, short replicationFactor){
        ListTopicsResult listTopicsResult = newAdminClient().listTopics();
        Set<String> topics = listTopicsResult.names().get();
        if(topics.contains(topicName)){
            return;
        }
        log.info("[deploy][kafka] create topic {}, all topics are {}", topicName, String.join(",", topics));
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        CreateTopicsResult result = newAdminClient().createTopics(Collections.singletonList(newTopic));
        result.all().get(10, TimeUnit.SECONDS);
    }

    @SneakyThrows
    public void deleteTopic(String topic){
        DeleteTopicsResult deleteTopicsResult = newAdminClient().deleteTopics(Collections.singletonList(topic));
        deleteTopicsResult.all().get();
    }

    public Map<TopicPartition, Long> lagOf() throws TimeoutException {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapservers);
        ListConsumerGroupOffsetsResult result = newAdminClient().listConsumerGroupOffsets(groupId);
        try {
            Map<TopicPartition, OffsetAndMetadata> consumedOffsets = result.partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 禁止自动提交位移
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumedOffsets.keySet());
                return endOffsets.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> entry.getValue() - consumedOffsets.get(entry.getKey()).offset()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 处理中断异常
            // ...
            return Collections.emptyMap();
        } catch (ExecutionException e) {
            // 处理ExecutionException
            // ...
            return Collections.emptyMap();
        } catch (TimeoutException e) {
            throw new TimeoutException("Timed out when getting lag for consumer group " + groupId);
        }
    }
}
