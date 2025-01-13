package com.virtual.cloud.om.log.service;

import com.virtual.cloud.om.log.entity.KafkaConsumerOffsetLog;
import com.virtual.cloud.om.log.entity.OperationLog;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsole;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: w22798
 * @Date: 2022/4/26 16:43
 */
@Service("operationLogApi")
@Slf4j
public class LogService implements ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private KafkaConsole kafkaConsole;


    /**
     * 创建对象
     * @param operationLog
     */
    public void saveLog(OperationLog operationLog) {
        mongoTemplate.save(operationLog);
    }

    public List<OperationLog> findLogs(OperationLog operationLog) {
        Query query=new Query(Criteria.where("deleted").is("n"));
        if(StringUtils.isNotEmpty(operationLog.getDesc())){
            query.addCriteria(Criteria.where("desc").is(operationLog.getDesc()));
        } else if(StringUtils.isNotEmpty(operationLog.getResult())){
            query.addCriteria(Criteria.where("result").is(operationLog.getResult()));
        } else if(StringUtils.isNotEmpty(operationLog.getModule())){
            query.addCriteria(Criteria.where("module").is(operationLog.getModule()));
        } else if(StringUtils.isNotEmpty(operationLog.getTime())){
            query.addCriteria(Criteria.where("time").gt(operationLog.getTime()));
        }
        List<OperationLog> operationLogs =  mongoTemplate.find(query , OperationLog.class);
        return operationLogs;
    }

    public long deleteLogBeforeTime(String time, boolean persistent) {
        Query query=new Query(Criteria.where("time").lt(time));
        if(persistent){
            return mongoTemplate.remove(query, OperationLogVO.class).getDeletedCount();
        } else {
            Update update= new Update().set("deleted", "y");
            //更新查询返回结果集的第一条
//            UpdateResult result =mongoTemplate.updateFirst(query,update,OperationLogVO.class);
            //更新查询返回结果集的所有
             return mongoTemplate.updateMulti(query,update, OperationLogVO.class).getMatchedCount();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            kafkaConsole.createTopic(Constant.KAFKA_TOPIC_LOG, 2, (short) 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 构建kafka消费者偏移量日志
     **/
    public void saveOrUpdateKafkaConsumerOffsetLog(String topic, Integer partition, String groupId, Long offset, Exception e){
        KafkaConsumerOffsetLog kafkaConsumerOffsetLog = queryKafkaConsumerOffsetLog(topic, partition, groupId);
        String id = generateConsumerOffsetLogId(topic, partition, groupId);
        kafkaConsumerOffsetLog.setId(id);
        kafkaConsumerOffsetLog.setTopic(topic);
        kafkaConsumerOffsetLog.setGroupId(groupId);
        kafkaConsumerOffsetLog.setPartition(partition);
        kafkaConsumerOffsetLog.setOffset(offset);
        kafkaConsumerOffsetLog.setTime(new Date());
        kafkaConsumerOffsetLog.setException(e);
        mongoTemplate.save(kafkaConsumerOffsetLog);
    }

    public KafkaConsumerOffsetLog queryKafkaConsumerOffsetLog(String topic, Integer partition, String groupId){
        String id = generateConsumerOffsetLogId(topic, partition, groupId);
        return Optional.ofNullable(mongoTemplate.findById(id, KafkaConsumerOffsetLog.class)).orElseGet(KafkaConsumerOffsetLog::new);
    }

    /**
     * @Description 生成消费者偏移量日志主键
     **/
    public String  generateConsumerOffsetLogId(String topic,Integer partition,String groupId){
        StringBuilder sb = new StringBuilder();
        sb.append(topic);
        sb.append("-");
        sb.append(partition);
        sb.append("-");
        sb.append(groupId);
        return sb.toString();
    }
}
