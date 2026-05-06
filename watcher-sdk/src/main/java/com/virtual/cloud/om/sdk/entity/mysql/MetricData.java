package com.virtual.cloud.om.sdk.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 指标数据实体 - 映射 metric_data 表
 */
@Data
@TableName("metric_data")
public class MetricData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String resourceId;

    private String resourceIp;

    private String platform;

    private String metricType;

    private String metricName;

    private String metricValue;

    private String metricUnit;

    private String tags;

    private LocalDateTime reportTime;

    private LocalDateTime createTime;
}
