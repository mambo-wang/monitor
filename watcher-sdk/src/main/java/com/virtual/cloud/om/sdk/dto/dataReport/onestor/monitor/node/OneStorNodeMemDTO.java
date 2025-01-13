package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.abs.OneStorMemUsageMonitorReportDTOAbs;
import lombok.Data;

/**
 * @author:XK
 * @Date:2022/12/12 23:23
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorNodeMemDTO extends OneStorMemUsageMonitorReportDTOAbs {
}
