package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor flow 监控信息 上报DTO公用字段
 * node_flow
 * host_flow
 * stor_cluster_flow
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OneStorFlowMonitorReportDTOAbs {

    private double storageReadFlow;

    private double storageWriteFlow;

    private double storageRecoverFlow;

    private double fsReadFlow;

    private double fsWriteFlow;




}
