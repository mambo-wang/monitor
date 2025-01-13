package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorFlowMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 存储池 flow 监控信息 上报DTO
 * stor_cluster_flow
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorClusterFlowMonitorReportDTO extends OneStorFlowMonitorReportDTOAbs {
    private String fs_id;
}
