package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorMemUsageMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 存储池 mem_usage 监控信息 上报DTO
 * stor_cluster_mem_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorClusterMemUsageMonitorReportDTO extends OneStorMemUsageMonitorReportDTOAbs {
    private String fs_id;
}
