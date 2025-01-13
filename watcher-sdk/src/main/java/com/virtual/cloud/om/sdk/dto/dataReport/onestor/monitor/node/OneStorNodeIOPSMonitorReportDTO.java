package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorIOPSMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor 节点池 iops 监控信息 上报DTO
 * node_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorNodeIOPSMonitorReportDTO extends OneStorIOPSMonitorReportDTOAbs {
    private String poolName;
//    "allIops": 1.111,
//            "fsTotalOps": 1.111
//
//            "allBw":1.111
//
//            "fsTotalBw":1.111
    private Double allIops;
    private Double fsTotalOps;
}
