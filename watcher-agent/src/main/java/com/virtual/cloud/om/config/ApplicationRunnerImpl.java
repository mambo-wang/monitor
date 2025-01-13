package com.virtual.cloud.om.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.entity.*;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            this.initAgentUniqueCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.initSysUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.initStep();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.initPwdStrategy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**第一次安装启动生成，后续替包升级不变,除非重装*/
    private void initAgentUniqueCode(){
        AgentUniqueCode agentUniqueCode = mongoTemplate.findOne(new Query(), AgentUniqueCode.class);
        if (Objects.nonNull(agentUniqueCode)){
            return;
        }
        AgentUniqueCode agentUnique = new AgentUniqueCode();
        Random random = new Random();
        String uuid = UUID.randomUUID().toString().replace("-","") + random.nextInt(1000);
        agentUnique.setUId(uuid);
        this.mongoTemplate.save(agentUnique);
    }

    private void initSysUser(){
        //根据用户名获取用户信息
        Query query=new Query(Criteria.where("username").is(Constant.username));
        SysUser user = mongoTemplate.findOne(query, SysUser.class);
        if (Objects.nonNull(user)){
            return;
        }
        SysUser sysUser = new SysUser();
        sysUser.setUsername(Constant.username);
        sysUser.setPassword(SM4Utils.webEncryptText(Constant.password));
        this.mongoTemplate.save(sysUser);
    }

    private void initStep(){
        List<OadWatcherStatus> all = mongoTemplate.findAll(OadWatcherStatus.class);
        if (CollectionUtil.isNotEmpty(all)){
            return;
        }
        OadWatcherStatus oadWatcherStatus = new OadWatcherStatus();
        oadWatcherStatus.setStep(Constant.DataCenter.STEP_NETWORK);
        this.mongoTemplate.save(oadWatcherStatus);
    }

    private void initPwdStrategy(){
        List<PwdStrategy> pwdStrategies = mongoTemplate.findAll(PwdStrategy.class);
        PwdStrategy pwdStrategy = new PwdStrategy();
        if (CollUtil.isNotEmpty(pwdStrategies)){
            PwdStrategy strategy = pwdStrategies.get(0);
            pwdStrategy.setId(strategy.getId());
        }
        pwdStrategy.setMinLength(Constant.DataCenter.MINLENGTH);
        pwdStrategy.setPwdComplex(Constant.DataCenter.PWDCOMPLEX);
        pwdStrategy.setPwdLifeTime(Constant.DataCenter.PWDLIFETIME);
        this.mongoTemplate.save(pwdStrategy);
    }
}
