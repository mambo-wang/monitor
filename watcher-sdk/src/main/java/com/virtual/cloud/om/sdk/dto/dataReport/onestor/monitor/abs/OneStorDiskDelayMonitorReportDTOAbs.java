package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor disk_delay 监控信息 上报DTO公用字段
 * node_disk_delay
 * host_disk_delay
 * stor_cluster_disk_delay
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorDiskDelayMonitorReportDTOAbs {

    private double latRead;

    private double latWrite;


}
