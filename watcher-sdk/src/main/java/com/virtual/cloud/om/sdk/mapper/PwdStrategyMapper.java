package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.PwdStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密码策略 Mapper 接口
 */
@Mapper
public interface PwdStrategyMapper extends BaseMapper<PwdStrategy> {

}
