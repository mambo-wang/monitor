package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.Resource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源 Mapper 接口
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

}
