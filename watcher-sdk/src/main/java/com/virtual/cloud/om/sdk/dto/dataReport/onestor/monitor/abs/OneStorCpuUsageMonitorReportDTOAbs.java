package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor cpu_usage 监控信息 上报DTO公用字段
 * node_cpu_usage
 * host_cpu_usage
 * stor_cluster_cpu_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorCpuUsageMonitorReportDTOAbs {

    private double cpuRatio;


}
