package com.virtual.cloud.om.agent.dto;

import lombok.Data;

import java.util.List;

/**
 * 指标趋势 DTO
 * 用于大屏概览功能，传输 CPU/内存等监控指标的时间序列数据
 */
@Data
public class MetricTrendDTO {
    
    /**
     * 资源 ID
     */
    private Long resourceId;
    
    /**
     * 资源名称
     */
    private String resourceName;
    
    /**
     * 指标类型：CPU, MEMORY
     */
    private String metricType;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 指标值列表
     */
    private List<Double> values;
    
    /**
     * 时间戳列表（毫秒）
     */
    private List<Long> timestamps;
    
    /**
     * 开始时间
     */
    private Long startTime;
    
    /**
     * 结束时间
     */
    private Long endTime;
    
    /**
     * 时间范围描述（如 "1h", "24h"）
     */
    private String timeRange;
}