package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor mem_usage 监控信息 上报DTO公用字段
 * node_mem_usage
 * host_mem_usage
 * stor_cluster_mem_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorMemUsageMonitorReportDTOAbs {
    private String poolName;

    private double memRatio;


}
