package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorFlowMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 主机 flow 监控信息 上报DTO
 * host_flow
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostFlowMonitorReportDTO extends OneStorFlowMonitorReportDTOAbs {
    private String hostName;
}
