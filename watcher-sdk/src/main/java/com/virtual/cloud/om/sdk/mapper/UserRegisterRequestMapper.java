package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户注册申请 Mapper 接口
 */
@Mapper
public interface UserRegisterRequestMapper extends BaseMapper<UserRegisterRequest> {

}
