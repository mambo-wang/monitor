package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
