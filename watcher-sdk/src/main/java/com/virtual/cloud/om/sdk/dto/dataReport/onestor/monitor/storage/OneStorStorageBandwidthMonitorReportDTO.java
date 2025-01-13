package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorBandwidthMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor ？？ bandwidth 监控信息 上报DTO
 * storage_bandwidth
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorStorageBandwidthMonitorReportDTO extends OneStorBandwidthMonitorReportDTOAbs {
    private String nodePoolName;
}
