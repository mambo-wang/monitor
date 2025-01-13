package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorBandwidthMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 主机 bandwidth 监控信息 上报DTO
 * host_bandwidth
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostBandwidthMonitorReportDTO extends OneStorBandwidthMonitorReportDTOAbs {
    private String hostName;
}
