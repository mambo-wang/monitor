package com.virtual.cloud.om.agent.service.parameter;

import com.virtual.cloud.om.agent.entity.Parameter;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * @Author: w22798
 * @Date: 2022/5/5 11:17
 */
@Service
@Slf4j
public class ParameterService implements ParameterApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取参数
     * @param type 类型
     * @param name 名称
     * @return
     */
    @Override
    public Optional<String> queryParameterByTypeAndName(String type, String name) {
        Query query=new Query(Criteria.where("type").is(type)).addCriteria(Criteria.where("name").is(name));
        Parameter parameter =  mongoTemplate.findOne(query , Parameter.class);
        return Optional.ofNullable(parameter).map(Parameter::getValue);
    }

    /**
     * 添加或者修改参数
     * @param value 值
     * @param type 类型
     * @param name 名称
     */
    @Override
    public void editParamByTypeAndName(String value, String type, String name){
        Query query=new Query(Criteria.where("type").is(type)).addCriteria(Criteria.where("name").is(name));
        boolean exists = mongoTemplate.exists(query, Parameter.class);
        if(exists){
            Update update= new Update().set("value", value);
            mongoTemplate.updateFirst(query,update,Parameter.class);
        } else {
            Parameter parameter = Parameter.builder().type(type).name(name).value(value).build();
            mongoTemplate.save(parameter);
        }
    }

    /**
     * 删除参数
     * @param type 类型
     * @param name 名称
     */
    @Override
    public void deleteParameters(String type, String name){
        Query query=new Query(Criteria.where("type").is(type)).addCriteria(Criteria.where("name").is(name));
        mongoTemplate.remove(query, Parameter.class);
    }
}
