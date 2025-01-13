package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.Rate;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/6/6 19:02
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DomainCpuUseCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        if (CollectionUtil.isEmpty(domainIds)) {
            List<String> domainIdsList = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
            if (CollectionUtil.isEmpty(domainIdsList)) {
                return Collections.emptyList();
            }
            domainIds = domainIdsList;
        }
        List<DataValueAndTagsDTO> finalResult = domainIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_CPU_USE, s);
            List<Rate> rates = null;
            try {
                rates = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas cpu_usage_detail is fail : " + e);
            }
            if (CollectionUtil.isEmpty(rates)) {
                return null;
            }
            Rate rate = rates.stream().sorted(Comparator.comparing(Rate::getTime).reversed()).collect(Collectors.toList()).stream().findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(rate.getRate());
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.DOMAIN_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        log.debug("[cpu_usage_detail]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cpu_usage_detail;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }
}
