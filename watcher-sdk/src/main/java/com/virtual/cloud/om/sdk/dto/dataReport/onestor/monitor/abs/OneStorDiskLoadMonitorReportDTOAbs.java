package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor disk_load 监控信息 上报DTO公用字段
 * node_disk_load
 * host_disk_load
 * stor_cluster_disk_load
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorDiskLoadMonitorReportDTOAbs {

    private double utilAvg;

    private double utilMax;



}
