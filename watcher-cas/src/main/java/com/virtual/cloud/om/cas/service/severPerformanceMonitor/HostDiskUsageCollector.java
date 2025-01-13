package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.Rates;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.TrendRatesDTO;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/6/1 17:42
 */

/**
 * 主机磁盘利用率
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostDiskUsageCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostDiskUsage(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainDiskUsage(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAll(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[disk_usage]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getHostDiskUsage(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds;
        if (CollectionUtil.isEmpty(hostIdList)) {
            hostIds = getId(Constant.Tags.HOST_IDS, tags);
        } else {
            hostIds = hostIdList;
        }
        if (hostIds.contains(Constant.Tags.HOST_IDS_ALL_VALUE)){
            hostIds=this.findAllIds.getHostIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isEmpty(hostIds)) {
            return Collections.emptyList();
        }
        hostIds.parallelStream().forEach(s -> {
            String url = String.format(CasUriConstants.Host.QUERY_HOST_DISKS_USAGE, s);
            List<TrendRatesDTO> trendRatesDTOS = null;
            try {
                trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas disk_usage is fail : " + e);
            }
            if (CollectionUtil.isNotEmpty(trendRatesDTOS)) {
                List<DataValueAndTagsDTO> dataValueAndTagsDTOMap = trendRatesDTOS.parallelStream().map(t -> {
                    Rates rates = t.getRates().stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
                    DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
                    dataValueAndTagsDTO.setValue(rates.getRate());
                    dataValueAndTagsDTO.setTimestamp(Long.valueOf(rates.getTime()));
                    String dev = "dev=" + t.getName();
                    String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, s, dev);
                    dataValueAndTagsDTO.setTags(tagsTo);
                    return dataValueAndTagsDTO;
                }).collect(Collectors.toList());
                dataValueAndTagsDTOS.addAll(dataValueAndTagsDTOMap);
            }

        });
        return dataValueAndTagsDTOS;

    }

    private List<DataValueAndTagsDTO> getDomainDiskUsage(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isEmpty(domainIds)) {
            return Collections.emptyList();
        }
        domainIds.parallelStream().forEach(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_DISK_USAGE, s);
            List<TrendRatesDTO> trendRatesDTOS = null;
            try {
                trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas disk_usage is fail : " + e);
            }
            if (CollectionUtil.isNotEmpty(trendRatesDTOS)) {
                List<DataValueAndTagsDTO> dataValueAndTagsDTOList = trendRatesDTOS.stream().map(t -> {
                    Rates rates = t.getRates().stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
                    DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
                    dataValueAndTagsDTO.setValue(rates.getRate());
                    dataValueAndTagsDTO.setTimestamp(Long.valueOf(rates.getTime()));
                    String dev = "dev=" + t.getName();
                    String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.DOMAIN_ID, s, dev);
                    dataValueAndTagsDTO.setTags(tagsTo);
                    return dataValueAndTagsDTO;
                }).collect(Collectors.toList());
                dataValueAndTagsDTOS.addAll(dataValueAndTagsDTOList);
            }
        });
        return dataValueAndTagsDTOS;
    }

    private List<DataValueAndTagsDTO> getAll(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostIoIops = this.getHostDiskUsage(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainIoIops = this.getDomainDiskUsage(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostIoIops.addAll(domainIoIops);
        return hostIoIops;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.disk_usage;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }

}
