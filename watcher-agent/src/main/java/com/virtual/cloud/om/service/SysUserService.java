package com.virtual.cloud.om.service;

import com.virtual.cloud.om.entity.SysUser;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class SysUserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void updateLastLoginTime(String username) {
        SysUser user = findUserByUserName(Constant.username);
        user.setLastLoginTime(new Date());
        mongoTemplate.save(user);
    }
    public SysUser findUserByUserName(String username){
        //根据用户名获取用户信息
        Query query=new Query(Criteria.where("username").is(username));
        SysUser sysUser = mongoTemplate.findOne(query, SysUser.class);
        if (Objects.isNull(sysUser)){
            throw new AppException(ErrorCodes.USER_DOES_NOT_EXIT);
        }
        return sysUser;
    }
}