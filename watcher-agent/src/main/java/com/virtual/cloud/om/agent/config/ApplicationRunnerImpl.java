package com.virtual.cloud.om.agent.config;

import cn.hutool.core.collection.CollUtil;
import com.virtual.cloud.om.sdk.entity.mysql.AgentUniqueCode;
import com.virtual.cloud.om.sdk.entity.mysql.OadWatcherStatus;
import com.virtual.cloud.om.sdk.entity.mysql.PwdStrategy;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.mapper.AgentUniqueCodeMapper;
import com.virtual.cloud.om.sdk.mapper.OadWatcherStatusMapper;
import com.virtual.cloud.om.sdk.mapper.PwdStrategyMapper;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private AgentUniqueCodeMapper agentUniqueCodeMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private OadWatcherStatusMapper oadWatcherStatusMapper;

    @Autowired
    private PwdStrategyMapper pwdStrategyMapper;

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
        List<AgentUniqueCode> list = agentUniqueCodeMapper.selectList(null);
        if (Objects.nonNull(list) && !list.isEmpty()){
            return;
        }
        AgentUniqueCode agentUnique = new AgentUniqueCode();
        Random random = new Random();
        String uuid = UUID.randomUUID().toString().replace("-","") + random.nextInt(1000);
        agentUnique.setId(UUID.randomUUID().toString());
        agentUnique.setUid(uuid);
        this.agentUniqueCodeMapper.insert(agentUnique);
    }

    private void initSysUser(){
        //根据用户名获取用户信息
        List<SysUser> users = sysUserMapper.selectList(null);
        if (Objects.nonNull(users) && !users.isEmpty()){
            return;
        }
        SysUser sysUser = new SysUser();
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setUsername(Constant.username);
        sysUser.setPassword(SM4Utils.webEncryptText(Constant.password));
        this.sysUserMapper.insert(sysUser);
    }

    private void initStep(){
        List<OadWatcherStatus> all = oadWatcherStatusMapper.selectList(null);
        if (Objects.nonNull(all) && !all.isEmpty()){
            return;
        }
        OadWatcherStatus oadWatcherStatus = new OadWatcherStatus();
        oadWatcherStatus.setId(UUID.randomUUID().toString());
        oadWatcherStatus.setStep(Constant.DataCenter.STEP_NETWORK);
        this.oadWatcherStatusMapper.insert(oadWatcherStatus);
    }

    private void initPwdStrategy(){
        List<PwdStrategy> pwdStrategies = pwdStrategyMapper.selectList(null);
        PwdStrategy pwdStrategy = new PwdStrategy();
        if (CollUtil.isNotEmpty(pwdStrategies)){
            PwdStrategy strategy = pwdStrategies.get(0);
            pwdStrategy.setId(strategy.getId());
        } else {
            pwdStrategy.setId(UUID.randomUUID().toString());
        }
        pwdStrategy.setMinLength(Constant.DataCenter.MINLENGTH);
        pwdStrategy.setPwdComplex(Constant.DataCenter.PWDCOMPLEX);
        pwdStrategy.setPwdLifeTime(Constant.DataCenter.PWDLIFETIME);
        if (CollUtil.isNotEmpty(pwdStrategies)) {
            this.pwdStrategyMapper.updateById(pwdStrategy);
        } else {
            this.pwdStrategyMapper.insert(pwdStrategy);
        }
    }
}
