package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.MetricData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 指标数据 Mapper 接口
 */
@Mapper
public interface MetricDataMapper extends BaseMapper<MetricData> {

}
