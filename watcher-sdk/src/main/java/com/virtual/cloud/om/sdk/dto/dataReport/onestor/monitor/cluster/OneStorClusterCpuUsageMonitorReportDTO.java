package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorCpuUsageMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 存储池 cpu_usage 监控信息 上报DTO
 * stor_cluster_cpu_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorClusterCpuUsageMonitorReportDTO extends OneStorCpuUsageMonitorReportDTOAbs {
    private String fs_id;
}
