package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorCpuUsageMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 节点池 CPU 监控信息 上报DTO
 * node_cpu_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorNodeCpuUsageMonitorReportDTO extends OneStorCpuUsageMonitorReportDTOAbs {
    private String poolName;
}
