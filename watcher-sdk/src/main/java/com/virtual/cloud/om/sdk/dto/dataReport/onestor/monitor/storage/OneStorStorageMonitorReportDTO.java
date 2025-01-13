package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorIOPSMonitorReportDTOAbs;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Onestor ？？ IOPS 监控信息 上报DTO
 * storage_iops
 * */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorStorageMonitorReportDTO extends OneStorIOPSMonitorReportDTOAbs {
    private String nodePoolName;
    private Double allIops;
    private Double allBw;
}
