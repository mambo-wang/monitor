package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.IoWriteAndReadDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.Rates;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.TrendRatesDTO;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/5/24 16:15
 */

/**
 * ***
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostDiskIopsCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.CLUSTER_IDS)) {
            finalResult = this.getClusterIoIops(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostIoIops(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainIoIops(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAll(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[disk_iops]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getHostIoIops(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds;
        if (CollectionUtil.isEmpty(hostIdList)) {
            hostIds = getId(Constant.Tags.HOST_IDS, tags);
        } else {
            hostIds = hostIdList;
        }
        if (hostIds.contains(Constant.Tags.HOST_IDS_ALL_VALUE)){
            hostIds=this.findAllIds.getHostIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOList = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isNotEmpty(hostIds)) {
            CompletableFuture[] completableFutures = hostIds.parallelStream().map(s ->
                    CompletableFuture.supplyAsync(() -> {
                        String url = String.format(CasUriConstants.Host.QUERY_HOST_DISKS_IOPS, s);
                        List<TrendRatesDTO> trendRatesDTOS = null;
                        try {
                            trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                                    username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                                    });
                        } catch (Exception e) {
                            log.error("cas host_cpu_usage is fail : " + e);
                        }
                        if (CollectionUtil.isEmpty(trendRatesDTOS)) {
                            return null;
                        }
                        List<DataValueAndTagsDTO> oneDiskValue = null;
                        try {
                            oneDiskValue = this.getOneDiskValue(trendRatesDTOS, resourceId, Constant.Tags.HOST_ID, s);
                        } catch (Exception e) {
                            log.error("cas [host_cpu_usage] is fail : " + e);
                        }
                        return oneDiskValue;
                    }).whenCompleteAsync((result, throwable) -> dataValueAndTagsDTOList.addAll(result))
            ).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(completableFutures).join();
        }
        return dataValueAndTagsDTOList;
    }

    private List<DataValueAndTagsDTO> getClusterIoIops(String platform, List<String> ClusterList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> clusterIds;
        if (CollectionUtil.isEmpty(ClusterList)) {
            clusterIds = getId(Constant.Tags.CLUSTER_IDS, tags);
        } else {
            clusterIds = ClusterList;
        }
        if (clusterIds.contains(Constant.Tags.CULSTER_IDS_ALL_VALUE)){
            clusterIds=this.findAllIds.getClusterIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        if (CollectionUtil.isNotEmpty(clusterIds)) {
            List<DataValueAndTagsDTO> dataValueAndTagsDTOList = clusterIds.parallelStream().map(s -> {
                String url = String.format(CasUriConstants.Cluster.QUERY_CLUSTER_DISK_IOPS, s);
                List<TrendRatesDTO> trendRatesDTOS = null;
                try {
                    trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                            });
                } catch (Exception e) {
                    log.error("cas disk_iops is fail : " + e);
                }
                if (CollectionUtil.isEmpty(trendRatesDTOS)) {
                    return null;
                }
                List<DataValueAndTagsDTO> oneDiskValue = null;
                try {
                    oneDiskValue = this.getOneDiskValue(trendRatesDTOS, resourceId, Constant.Tags.CLUSTER_ID, s);
                } catch (Exception e) {
                    log.error("cas [disk_iops] fail : " + e);
                }
                return oneDiskValue;
            }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
            return dataValueAndTagsDTOList;
        }
        return Collections.emptyList();
    }

    private List<DataValueAndTagsDTO> getDomainIoIops(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOList = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isNotEmpty(domainIds)) {
            List<DataValueAndTagsDTO> result = domainIds.parallelStream().map(s -> {
                String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_IO_IOPS, s);
                List<TrendRatesDTO> trendRatesDTOS = null;
                try {
                    trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                            });
                } catch (Exception e) {
                    log.error("cas disk_iops is fail : " + e);
                }
                if (CollectionUtil.isEmpty(trendRatesDTOS)) {
                    return null;
                }
                List<DataValueAndTagsDTO> oneDiskValue = null;
                try {
                    oneDiskValue = this.getOneDiskValue(trendRatesDTOS, resourceId, Constant.Tags.DOMAIN_ID, s);
                } catch (Exception e) {
                    log.error("cas [disk_iops] is fail : " + e);
                }
                return oneDiskValue;
            }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
            dataValueAndTagsDTOList.addAll(result);
        }
        return dataValueAndTagsDTOList;
    }


    private List<DataValueAndTagsDTO> getOneDiskValue(List<TrendRatesDTO> trendRatesDTOS, String resourceId, String type, String id) {
        List<TrendRatesDTO> readList = trendRatesDTOS.parallelStream().filter(t -> t.getName().endsWith("读")).collect(Collectors.toList());
        List<TrendRatesDTO> writeList = trendRatesDTOS.parallelStream().filter(t -> t.getName().endsWith("写")).collect(Collectors.toList());
        Map<String, List<Rates>> readMap = readList.parallelStream().collect(Collectors.toMap(TrendRatesDTO::getName, TrendRatesDTO::getRates));
        List<DataValueAndTagsDTO> resuleOne = writeList.parallelStream().map(w -> {
            List<Rates> read = readMap.get(w.getName().replace("-写", "-读"));
            Rates readRates = read.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            Map<String, String> writeRateMap = w.getRates().stream().collect(Collectors.toMap(Rates::getTime, Rates::getRate));
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            IoWriteAndReadDTO ioWriteAndReadDTO = new IoWriteAndReadDTO();
            ioWriteAndReadDTO.setRead(Double.valueOf(readRates.getRate()));
            String writeRate = writeRateMap.get(readRates.getTime());
            ioWriteAndReadDTO.setWrite(Double.valueOf(writeRate));
            dataValueAndTagsDTO.setValue(ioWriteAndReadDTO);
            String dev = "dev=" + w.getName().replace("-写", "");
            String tagsTo = TagsUtil.buildTags(resourceId, type, id, dev);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(readRates.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return resuleOne;
    }


    private List<DataValueAndTagsDTO> getAll(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> clusterIds = findAllIds.getClusterIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostIoIops = this.getHostIoIops(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> clusterIoIops = this.getClusterIoIops(platform, clusterIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainIoIops = this.getDomainIoIops(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostIoIops.addAll(clusterIoIops);
        hostIoIops.addAll(domainIoIops);
        return hostIoIops;

    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.disk_iops;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }


}
