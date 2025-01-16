package com.virtual.cloud.om.agent.service.warn;

import com.virtual.cloud.om.agent.entity.Warn;
import com.virtual.cloud.om.sdk.dto.WarnDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WarnMgrApimpl implements WarnMgrApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取告警信息
     * @param resourceId 资源id
     * @param type 告警类型
     * @return
     */
    public WarnDTO queryWarnByIdAndType(String resourceId, String type) {
        Query query=new Query(Criteria.where("resourceId").is(resourceId)).addCriteria(Criteria.where("type").is(type));
        Warn warn =  mongoTemplate.findOne(query , Warn.class);
        if (ObjectUtils.isEmpty(warn)){
            return null;
        }
        return this.convertToDTO(warn);
    }

    /**
     * 添加或者修改告警信息
     * @param resourceId 资源id
     * @param eventTime 最新告警时间
     */
    public void editWarnByResourceIdAndType(String resourceId, Long eventTime,String type,Long reportTime){
        Query query=new Query(Criteria.where("resourceId").is(resourceId)).addCriteria(Criteria.where("type").is(type));
        boolean exists = mongoTemplate.exists(query, Warn.class);
        if(exists){
            Update update= new Update().set("eventTime",eventTime).set("reportTime",reportTime);
            mongoTemplate.updateFirst(query,update, Warn.class);
        } else {
            Warn warn = Warn.builder().resourceId(resourceId).eventTime(eventTime).type(type).reportTime(reportTime).build();
            mongoTemplate.save(warn);
        }
    }

    @Override
    public void editWarnByResourceId(String resourceId) {
        Query query=new Query(Criteria.where("resourceId").is(resourceId));
        boolean exists = mongoTemplate.exists(query, Warn.class);
        if(exists){
            Update update= new Update().set("reportTime",System.currentTimeMillis());
            mongoTemplate.updateFirst(query,update, Warn.class);
        } else {
           return;
        }
    }

    public Warn convertToEntity(WarnDTO warnDTO){
        Warn dto =new Warn();
        BeanUtils.copyProperties(warnDTO,dto);
        return dto;
    }

    public WarnDTO convertToDTO(Warn warn){
        WarnDTO warnDTO =new WarnDTO();
        BeanUtils.copyProperties(warn,warnDTO);
        return warnDTO;
    }

}
