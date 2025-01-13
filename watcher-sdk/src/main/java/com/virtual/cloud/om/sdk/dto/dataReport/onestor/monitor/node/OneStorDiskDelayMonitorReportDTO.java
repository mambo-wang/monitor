package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorDiskDelayMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 节点池 disk_delay 监控信息 上报DTO
 * node_disk_delay
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorDiskDelayMonitorReportDTO extends OneStorDiskDelayMonitorReportDTOAbs {
    private String poolName;

}
