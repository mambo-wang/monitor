package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorIOPSMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 存储池 IOPS 监控信息 上报DTO
 * stor_cluster_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorClusterIOPSMonitorReportDTO extends OneStorIOPSMonitorReportDTOAbs {
    private String fs_id;
}
