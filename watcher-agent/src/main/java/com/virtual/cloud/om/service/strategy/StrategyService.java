package com.virtual.cloud.om.service.strategy;

import com.virtual.cloud.om.dto.StrategyDTO;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/5/12 11:02
 */
public interface StrategyService {

    void taskserver(List<StrategyDTO> strategyDTOS);

    String getTaskId(String resourceId, ReportMetricEnum metricEnum);

}
