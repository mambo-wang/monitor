package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorMemUsageMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 主机 mem_usage 监控信息 上报DTO
 * host_mem_usage
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostMemUsageMonitorReportDTO extends OneStorMemUsageMonitorReportDTOAbs {
    private String hostName;
}
