package com.virtual.cloud.om.service.warn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.google.common.collect.Maps;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.api.ResourceApi;
import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnReportDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarnReportService {

    @Resource
    private WarnMgrApi warnMgrApi;

    private final DataCenterApi dataCenterApi;
    private final ResourceApi resourceApi;

    private final WarnReportCollector[] warnReportCollectors;


    private Map<WarnMetricEnum, WarnReportCollector> collectApiMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        Stream.of(warnReportCollectors).forEach(collectApi -> collectApiMap.put(collectApi.metric(), collectApi));
    }

    /**
     * 告警信息上报
     */
    public void report(String tags, WarnMetricEnum metric) {
        WarnReportDTO dto = new WarnReportDTO();
        WarnReportCollector warnReportCollector = collectApiMap.get(metric);
        if (Objects.isNull(warnReportCollector)) {
            return;
        }
        // tags 中拆 resourceId
        Optional<String> first = warnReportCollector.getId("resourceId", tags).stream().findFirst();
        if (!first.isPresent()) {
            throw new AppException(ErrorCodes.RESTHOST_RESOURCEID_NONE);
        }
        String resourceId = first.get();
        RestHost restHost;
        ReportResourceEnum platform;
        boolean reportWatcher = resourceId.equals("watcher");
        if (reportWatcher) {
            platform = ReportResourceEnum.hccAgent;
            restHost = RestHost.builder().platform(platform.name()).build();
        } else {
            restHost = this.resourceApi.findRestHostByResourceId(resourceId);
            platform = ReportResourceEnum.valueOf(restHost.getPlatform());
            if (Objects.isNull(platform)) {
                log.info("[warn collectDataReportTypeByMetricEnum ][resourceId={}] platform is null ", resourceId);
                return;
            }
        }
        dto.setPlatform(platform);
        List<WarnDataDTO> data = Lists.newArrayList();
        try {
            data = warnReportCollector.data(restHost, tags);
        } catch (AppException e) {
            log.error(e.getMessage());
        }
        if (CollUtil.isNotEmpty(data)) {
            dto.setAlerts(data);
            final String traceId = UUID.fastUUID().toString();
            dto.setReportTimestamp(System.currentTimeMillis());
            dto.setTraceId(traceId);
            RpcResult rpcResult = null;
            try {
//todo wb
            } catch (AppException e) {
                // 数据中心返回了这个code，需要重新上报一次
                if (e.getErrorCode().equals(ErrorCodes.report_data_error_need_report_again)) {
                    try {
//todo wb
                    } catch (Exception e1) {
                        e.printStackTrace();
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            List<String> list = data.stream().map(WarnDataDTO::getResourceId).distinct().collect(Collectors.toList());
            list.forEach(s -> {
                warnMgrApi.editWarnByResourceId(s);
                log.info("[warn reportTime edit success]");
            });
        }
    }
}
