package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorDiskDelayMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 存储池 disk_delay 监控信息 上报DTO
 * stor_cluster_disk_delay
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorClusterDiskDelayMonitorReportDTO extends OneStorDiskDelayMonitorReportDTOAbs {
    private String fs_id;

}
