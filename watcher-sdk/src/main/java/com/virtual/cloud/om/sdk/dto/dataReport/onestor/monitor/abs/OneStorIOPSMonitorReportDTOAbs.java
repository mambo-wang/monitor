package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor IOPS 监控信息 上报DTO公用字段
 * node_iops
 * host_iops
 * storage_iops
 * stor_cluster_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorIOPSMonitorReportDTOAbs {
   public double iopsRead;

    public double iopsWrite;

    public double recoverOps;

    public double opsRead;

    public double opsWrite;



}
