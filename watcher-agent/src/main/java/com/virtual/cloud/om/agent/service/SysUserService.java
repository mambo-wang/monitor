package com.virtual.cloud.om.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    public void updateLastLoginTime(String username) {
        SysUser user = findUserByUserName(Constant.username);
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    public SysUser findUserByUserName(String username){
        //根据用户名获取用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (Objects.isNull(sysUser)){
            throw new AppException(ErrorCodes.USER_DOES_NOT_EXIT);
        }
        return sysUser;
    }
}
