package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.DomainPartitionDetailDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostPartitionDetailDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.PartitionUsageDTO;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/6/2 10:36
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostPartitionUsageCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostPartitionUsage(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainPartitionUsage(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAll(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[partition_usage]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getHostPartitionUsage(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds;
        if (CollectionUtil.isEmpty(hostIdList)) {
            hostIds = getId(Constant.Tags.HOST_IDS, tags);
        } else {
            hostIds = hostIdList;
        }
        if (CollectionUtil.isEmpty(hostIds)) {
            return Collections.emptyList();
        }
        if (hostIds.contains(Constant.Tags.HOST_IDS_ALL_VALUE)){
            hostIds=this.findAllIds.getHostIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        CompletableFuture[] completableFutures = hostIds.stream().map(s ->
                CompletableFuture.supplyAsync(() -> {
                    String url = String.format(CasUriConstants.Host.QUERY_HOST_PARTITION_USAGE, s);
                    List<HostPartitionDetailDTO> hostPartitionDetailDTOS = null;
                    try {
                        hostPartitionDetailDTOS = this.casRestConnection.get(platform, host, protocol, port,
                                username, password, url, new ParameterizedTypeReference<List<HostPartitionDetailDTO>>() {
                                });

                    } catch (Exception e) {
                        log.error("cas partition_usage is fail : " + e);
                    }
                    if (CollectionUtil.isEmpty(hostPartitionDetailDTOS)) {
                        return null;
                    }
                    DataValueAndTagsDTO oneData = this.getOneData(hostPartitionDetailDTOS, resourceId, Constant.Tags.HOST_ID, s);
                    return oneData;
                }).whenCompleteAsync((result, throwable) ->
                        {
                            if (Objects.nonNull(result)) {
                                dataValueAndTagsDTOS.add(result);
                            }
                        }
                )
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        return dataValueAndTagsDTOS.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<DataValueAndTagsDTO> getDomainPartitionUsage(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        if (CollectionUtil.isEmpty(domainIds)) {
            return Collections.emptyList();
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        CompletableFuture[] completableFutures = domainIds.stream().map(s ->
                CompletableFuture.supplyAsync(() -> {
                    String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_PARTITION_USAGE, s);
                    List<DomainPartitionDetailDTO> domainPartitionDetailDTOS = null;
                    try {
                        domainPartitionDetailDTOS = this.casRestConnection.get(platform, host, protocol, port,
                                username, password, url, new ParameterizedTypeReference<List<DomainPartitionDetailDTO>>() {
                                });

                    } catch (Exception e) {
                        log.error("cas partition_usage is fail : " + e);
                    }
                    if (CollectionUtil.isEmpty(domainPartitionDetailDTOS)) {
                        return null;
                    }
                    DataValueAndTagsDTO oneData = this.getOneDomainData(domainPartitionDetailDTOS, resourceId, Constant.Tags.DOMAIN_ID, s);
                    return oneData;
                }).whenCompleteAsync((result, throwable) -> {
                    if (Objects.nonNull(result)) {
                        dataValueAndTagsDTOS.add(result);
                    }
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        return dataValueAndTagsDTOS.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private DataValueAndTagsDTO getOneData(List<HostPartitionDetailDTO> hostPartitionDetailDTOS, String resourceId, String type, String id) {
        List<PartitionUsageDTO> partitionUsageDTOS = hostPartitionDetailDTOS.stream().map(h -> {
            PartitionUsageDTO partitionUsageDTO = new PartitionUsageDTO();
            partitionUsageDTO.setPartition(h.getPartitionName());
            partitionUsageDTO.setSize(Double.valueOf(h.getSize()));
            partitionUsageDTO.setUsed(Double.valueOf(h.getUsed()));
            partitionUsageDTO.setType(h.getPartitionType());
            partitionUsageDTO.setMountPoint(h.getMountedDir());
            partitionUsageDTO.setUtilization(Double.valueOf(h.getUtilization()));
            return partitionUsageDTO;
        }).collect(Collectors.toList());
        DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setValue(partitionUsageDTOS);
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        String tagsTo = TagsUtil.buildTags(resourceId, type, id);
        dataValueAndTagsDTO.setTags(tagsTo);
        return dataValueAndTagsDTO;
    }

    private DataValueAndTagsDTO getOneDomainData(List<DomainPartitionDetailDTO> domainPartitionDetailDTOS, String resourceId, String type, String id) {
        List<PartitionUsageDTO> partitionUsageDTOS = domainPartitionDetailDTOS.stream().map(h -> {
            PartitionUsageDTO partitionUsageDTO = new PartitionUsageDTO();
            partitionUsageDTO.setPartition(h.getPartitionName());
            partitionUsageDTO.setSize(Double.valueOf(h.getSize()));
            partitionUsageDTO.setUsed(Double.valueOf(h.getUsed()));
            partitionUsageDTO.setUtilization(h.getUtilization());
            return partitionUsageDTO;
        }).collect(Collectors.toList());
        DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setValue(partitionUsageDTOS);
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        String tagsTo = TagsUtil.buildTags(resourceId, type, id);
        dataValueAndTagsDTO.setTags(tagsTo);

        return dataValueAndTagsDTO;
    }

    private List<DataValueAndTagsDTO> getAll(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostIoIops = this.getHostPartitionUsage(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainIoIops = this.getDomainPartitionUsage(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostIoIops.addAll(domainIoIops);
        return hostIoIops;

    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.partition_usage;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
