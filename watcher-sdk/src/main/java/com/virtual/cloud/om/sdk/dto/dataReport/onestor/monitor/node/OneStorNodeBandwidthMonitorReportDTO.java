package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorBandwidthMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * Onestor 节点池 bandwidth 监控信息 上报DTO
 * node_bandwidth
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorNodeBandwidthMonitorReportDTO extends OneStorBandwidthMonitorReportDTOAbs {
    private String poolName;
    private Double allBw;

    private Double fsTotalBw;
}
