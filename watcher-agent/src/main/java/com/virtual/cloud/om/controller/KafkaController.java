package com.virtual.cloud.om.controller;

import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Api(tags = "kafka运维")
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private KafkaConsole kafkaConsole;

    @Resource
    private RealTimeLogApi realTimeLogApi;

    @ApiOperation(value = "查询主题")
    @GetMapping("/topic")
    public RpcResult queryTopicDesc(@RequestParam(name = "topic", defaultValue = Constant.KAFKA_TOPIC_FILEBEAT)String topic) throws ExecutionException, InterruptedException, TimeoutException {

        Map<String, TopicDescription> map = kafkaConsole.selectTopicInfo(topic);
        return RpcResult.success(map);
    }

    @ApiOperation(value = "增加分区")
    @GetMapping("/partition")
    public RpcResult increasePartition(@RequestParam(name = "topic", defaultValue = Constant.KAFKA_TOPIC_FILEBEAT)String topic,
                                        @RequestParam(name = "number")int number)  {

        kafkaConsole.increaseTopicPartitions(topic, number);

        return RpcResult.success("success");
    }

    @ApiOperation(value = "增加主题")
    @PostMapping("/topic")
    public RpcResult createTopic(@RequestParam(name = "topic")String topicName,
                            @RequestParam(name = "partition") int partition,
                            @RequestParam(name = "replication") short replication){
        kafkaConsole.createTopic(topicName, partition, replication);
        return RpcResult.success("success");
    }

    @ApiOperation(value = "删除主题")
    @DeleteMapping("/topic")
    public RpcResult createTopic(@RequestParam(name = "topic")String topicName){
        kafkaConsole.deleteTopic(topicName);
        return RpcResult.success("success");
    }

    @ApiOperation(value = "查询broker占用空间大小")
    @GetMapping("/size")
    public RpcResult queryBrokerUsedDiskSize() throws ExecutionException, InterruptedException {
        long space = kafkaConsole.getSpace();
        return RpcResult.success(space);
    }

    @ApiOperation(value = "查询消费者组lag值（消息堆积数量）")
    @GetMapping("/lags")
    public RpcResult<Map<TopicPartition, Long>> queryLag() throws TimeoutException {
        return RpcResult.success(kafkaConsole.lagOf());
    }

    @GetMapping("/createAndConsume")
    public RpcResult<Void> createAndConsumeFilebeatLogTopic(){
        realTimeLogApi.createAndConsumeFilebeatLogTopic(6, 2);
        return RpcResult.success();
    }
}
