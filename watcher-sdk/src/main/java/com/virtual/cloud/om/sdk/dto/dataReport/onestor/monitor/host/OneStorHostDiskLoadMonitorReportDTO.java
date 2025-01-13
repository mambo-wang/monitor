package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorDiskLoadMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 主机 disk_load 监控信息 上报DTO
 * host_disk_load
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostDiskLoadMonitorReportDTO extends OneStorDiskLoadMonitorReportDTOAbs {
    private String hostName;
    private String system;
}
