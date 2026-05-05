package com.virtual.cloud.om.agent.service.warn;

import com.virtual.cloud.om.sdk.api.ResourceApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 告警上报服务 - MySQL 单机版已简化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarnReportService {

    private final ResourceApi resourceApi;
    private final WarnReportCollector[] warnReportCollectors;

    private Map<WarnMetricEnum, WarnReportCollector> collectApiMap;

    @PostConstruct
    public void init() {
        if (warnReportCollectors != null) {
            collectApiMap = Stream.of(warnReportCollectors)
                    .collect(Collectors.toMap(WarnReportCollector::metric, c -> c, (a, b) -> a));
        }
        log.warn("[WarnReportService] 告警上报服务已简化，MongoDB版本已禁用");
    }

    /**
     * 告警信息上报 - 已简化
     */
    public void report(String tags, WarnMetricEnum metric) {
        log.debug("[WarnReportService] 告警上报功能已简化");
    }
}
