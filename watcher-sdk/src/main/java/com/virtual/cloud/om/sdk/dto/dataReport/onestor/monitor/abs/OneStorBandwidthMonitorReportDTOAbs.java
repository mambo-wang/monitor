package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor bandwidth 监控信息DTO公用字段
 * node_bandwidth
 * host_bandwidth
 * storage_bandwidth
 * stor_cluster_bandwidth
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorBandwidthMonitorReportDTOAbs {
    private double storageReadBw;

    private double storageWriteBw;

    private double storageRecoverBw;

    private double fsReadBw;

    private double fsWriteBw;



}
