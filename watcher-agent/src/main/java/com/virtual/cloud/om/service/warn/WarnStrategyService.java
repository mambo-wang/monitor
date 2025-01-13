package com.virtual.cloud.om.service.warn;

import com.virtual.cloud.om.dto.WarnStrategyDTO;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;

import java.util.List;

public interface WarnStrategyService {

    void pullWarnStrategyFromDataCenter();

    void taskserver(List<WarnStrategyDTO> warnStrategyDTOS);

    String getTaskId(String resourceId, ReportMetricEnum[] metricEnum);

}
