package com.virtual.cloud.om.agent.service.kafka;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.agent.entity.OperationLog;
import com.virtual.cloud.om.agent.service.logs.LogService;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.concurrent.CloudExecutorServices;
import com.virtual.cloud.om.sdk.config.elasticsearch.EsOperation;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsole;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsumerBuilder;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsumerProperties;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * kafka消费
 *
 * @Author: w22798
 * @Date: 2022/5/14 14:01
 */
@Component
@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
@DependsOn("esOperation")
public class KafkaConsumers {

    @Autowired
    private LogService logService;

    @Autowired
    private RealTimeLogApi realTimeLogApi;

    @Autowired
    private KafkaConsumerProperties kafkaConsumerProperties;

    @Autowired
    private DataCenterApi dataCenterApi;

    @Autowired
    private KafkaConsole kafkaConsole;

    @Resource
    private ParameterApi parameterApi;

    private String charsetName = "UTF-8";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Resource
    private EsOperation esOperation;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    ExecutorService executors = CloudExecutorServices.get().getKafkaService();

    @PostConstruct
    public void receiveTopicMsg() {
        try {
            createEsIndex();
            //主节服务启动时，判断部署完成了，创建主题topic
            if (dataCenterApi.getInitStep() >= Constant.DataCenter.STEP_DEPLOY) {
                Optional<String> vip = parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_VIP);
                if (vip.isPresent()) {
                    kafkaConsole.createTopic(Constant.KAFKA_TOPIC_FILEBEAT, 6, (short) 2);
                } else {
                    kafkaConsole.createTopic(Constant.KAFKA_TOPIC_FILEBEAT, 2, (short) 1);
                }
//                receiveFilebeatMsg();
            }
            receiveOpMsg();
        } catch (Exception e) {
            log.error("[kafka] consume error ", e);
        }
    }

    @SneakyThrows
    private void createEsIndex() {
        boolean exist = esOperation.checkIndex(Constant.RealtimeLog.ES_INDEX_NAME_LOG);
        // 4、处理响应结果
        log.info("索引存在于ES：" + exist);
        if(exist){
            return;
        }
        boolean result = esOperation.createIndex(Constant.RealtimeLog.ES_INDEX_NAME_LOG, "src/main/resources/es_mapping_serverlog.json");
        // 处理响应结果
        log.info("添加索引是否成功：" + result);
    }

    public void receiveOpMsg() {
        //页面操作 消息处理
        executors.execute(() -> {
            KafkaConsumer<String, String> kafkaOpLogConsumer = KafkaConsumerBuilder.kafkaConsumer(bootstrapServers, kafkaConsumerProperties, groupId);
            kafkaOpLogConsumer.subscribe(Collections.singletonList(Constant.KAFKA_TOPIC_LOG));
            startConsumeOpLog(kafkaOpLogConsumer, "kafkaOpLogConsumer");
        });
    }

    @SneakyThrows
    public void receiveFilebeatMsg() {
        //filebeat 消息处理1
        executors.execute(() -> {
            KafkaConsumer<String, String> kafkaFilebeatConsumer = KafkaConsumerBuilder.kafkaConsumer(bootstrapServers, kafkaConsumerProperties, groupId);
            kafkaFilebeatConsumer.subscribe(Collections.singletonList(Constant.KAFKA_TOPIC_FILEBEAT));
            startConsumeFileBeatLog(kafkaFilebeatConsumer, "kafkaFilebeatConsumer");
        });

        //filebeat 消息处理2
        executors.execute(() -> {
            KafkaConsumer<String, String> secondaryKafkaFilebeatConsumer = KafkaConsumerBuilder.kafkaConsumer(bootstrapServers, kafkaConsumerProperties, groupId);
            secondaryKafkaFilebeatConsumer.subscribe(Collections.singletonList(Constant.KAFKA_TOPIC_FILEBEAT));
            startConsumeFileBeatLog(secondaryKafkaFilebeatConsumer, "secondaryKafkaFilebeatConsumer");
        });
    }

    /**
     * 重设位移
     * 报错：java.lang.IllegalStateException: No current assignment for partition wukong-0
     * 脚本：./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group testGroup --reset-offsets --by-duration PT10H30M0S --execute --all-topics
     *
     * @param topic
     */
    public void seek(String host, String topic) {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":9092");

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties)) {
            consumer.subscribe(Collections.singleton(topic));
            consumer.poll(0);

            for (PartitionInfo info : consumer.partitionsFor(topic)) {
                TopicPartition tp = new TopicPartition(topic, info.partition());
                // 假设向前跳123条消息
                long targetOffset = consumer.committed(tp).offset() + 123L;
                consumer.seek(tp, targetOffset);
            }
        }
    }

    private void startConsumeFileBeatLog(KafkaConsumer consumer, String metadata) {
        try {
            while (true) {
                ConsumerRecords records = consumer.poll(Duration.ofSeconds(2));
                try {
                    if (records != null && !records.isEmpty()) {
                        log.debug("[kafka][filebeat] ----------------poll filebeat msg success from {} ------------------------", metadata);
                        processFilebeatLog(records);
                        consumer.commitAsync();
                    }
                } catch (Exception e) {
                    log.error("[kafka][filebeat] consume msg {} fail:{}",records, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    @SneakyThrows
    private void processFilebeatLog(ConsumerRecords<String, String> records) {
        Map<String, String> logPathTargetType = realTimeLogApi.queryLogPathTargetType();
        Map<String, String> logPathLogType = realTimeLogApi.queryLogPathLogType();
        for (ConsumerRecord<String, String> record : records) {
            // 1.获取kafkajsong数据
            String message = new String(record.value().getBytes(), charsetName);
            JSONObject jsonObject = JSONUtil.parseObj(message);

            String logPath = jsonObject.getJSONObject("log").getJSONObject("file").getStr("path");
            Optional<LogLine> logLineOptional = realTimeLogApi.parseLine(logPathLogType.get(logPath), jsonObject.getStr("message"));
            if(!logLineOptional.isPresent()){
                continue;
            }
            LogLine logLine = logLineOptional.get();
            logLine.setTargetType(logPathLogType.get(logPath));
            logLine.setPath(logPath);
            logLine.setPlatform(jsonObject.getJSONObject("fields").getStr("platform"));
            addTargetType(logLine, logPathTargetType, logPath);
            dealResourceInfo(logLine, jsonObject.getJSONObject("fields").getStr("tags"));
            saveToEs(logLine);
        }
    }

    public void saveToEs(LogLine data) {
        try {
            // 定义请求对象
            IndexRequest request = new IndexRequest(Constant.RealtimeLog.ES_INDEX_NAME_LOG);
            // 将json格式字符串放在请求中
            request.source(SerializeUtils.toJson(data), XContentType.JSON);
            // 3、发送请求到ES
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            // 4、处理响应结果
            log.info("数据插入结果：" + response.getResult());
        } catch (Exception e) {
            log.error("[logs sink][error]:{};[errorData]:{}", e, data);
        }
    }

    private void addTargetType(LogLine logLine, Map<String, String> logPathTargetType, String logPath) {
        if (logPath.startsWith("/var/log/libvirt/qemu/")) {
            String targetType = logPathTargetType.get("/var/log/libvirt/qemu/*.log");
            logLine.setTargetType(targetType);
        } else {
            String targetType = logPathTargetType.get(logPath);
            logLine.setTargetType(targetType);
        }
    }

    private String formatTimeStamp(String timeStamp) {
        DateTime dateTime = DateUtil.parseUTC(timeStamp);
        String result = dateTime.toStringDefaultTimeZone();
        return result;
    }

    private void startConsumeOpLog(KafkaConsumer consumer, String metadata) {
        try {
            while (true) {
                try {
                    ConsumerRecords records = consumer.poll(Duration.ofSeconds(2));
                    if (records != null && !records.isEmpty()) {
                        processOpLog(records);
                        consumer.commitAsync(new GeneralOffsetCommitCallback(groupId));
                    }
                } catch (Exception e) {
                    log.error("[kafka][oplog] convert message fail", e);
                }
            }
        } catch (Exception e) {
            log.error("[kafka][oplog] consumer exception", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void dealResourceInfo(LogLine logLine, String tags){
        //tags的格式为“resourceId=317;hostId=1;hostIP=10.99.224.137;hostName=cvknode1;”
        List<String> tagList = Arrays.asList(tags.split(";"));
        String[] resourceIdTag = tagList.get(0).split("=");
        String resourceId = resourceIdTag.length > 1 ? resourceIdTag[1] : null;

        String[] hostIdTag = tagList.get(1).split("=");
        String hostId = hostIdTag.length > 1 ? hostIdTag[1] : null;

        String[] hostIPTag = tagList.get(2).split("=");
        String hostIP = hostIPTag.length > 1 ? hostIPTag[1] : null;

        String[] hostNameTag = tagList.get(3).split("=");
        String hostName = hostNameTag.length > 1 ? hostNameTag[1] : null;

        logLine.setResourceId(resourceId);
        logLine.setHostId(hostId);
        logLine.setHostIp(hostIP);
        logLine.setHostName(hostName);
    }

    @SneakyThrows
    private void processOpLog(ConsumerRecords<String, String> records) {

        for (ConsumerRecord<String, String> record : records) {
            // 1.获取kafkajsong数据
            String message = new String(record.value().getBytes(), charsetName);
            log.info("[kafka][oplog] poll operation msg success, content: {}", message);
            OperationLog operationLog = SerializeUtils.json2Object(message, OperationLog.class);
            logService.saveLog(operationLog);
        }
    }
}
