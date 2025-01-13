package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.disk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorBandwidthMonitorReportDTOAbs;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorCapacityMonitorReportDTOAbs;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorIOPSMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 硬盘池 capacity 监控信息 上报DTO
 * host_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorDiskPoolCapacityMonitorReportDTO extends OneStorCapacityMonitorReportDTOAbs {
    private String diskpoolName;
}
