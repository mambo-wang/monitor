package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorIOPSMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 主机 iops 监控信息 上报DTO
 * host_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostIOPSMonitorReportDTO extends OneStorIOPSMonitorReportDTOAbs {
    private String hostName;
}
