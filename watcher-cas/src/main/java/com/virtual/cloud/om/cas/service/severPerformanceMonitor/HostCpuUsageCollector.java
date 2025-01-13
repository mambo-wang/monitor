package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/5/24 9:44
 */

/**
 * 主机cpu利用率 1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostCpuUsageCollector extends DataReportCollector {

    private final CasRestConnection casRestConnection;

    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.CLUSTER_IDS)) {
            finalResult = this.getClusterCpu(platform, Collections.EMPTY_LIST, host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostCpu(platform, Collections.EMPTY_LIST, host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainCpu(platform, Collections.EMPTY_LIST, host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAllCpu(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[cpu_usage]==================>>采集完成：size=" + finalResult.size() + "finalResult: " + finalResult);
        return finalResult;

    }

    private List<DataValueAndTagsDTO> getHostCpu(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
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
        List<DataValueAndTagsDTO> finalResult = hostIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Host.QUERY_HOST_CPU_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas host_cpu_usage is fail : " + e);
                throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setValue(rate.getRate());
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getClusterCpu(String platform, List<String> ClusterList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> clusterIds;
        if (CollectionUtil.isEmpty(ClusterList)) {
            clusterIds = getId(Constant.Tags.CLUSTER_IDS, tags);
        } else {
            clusterIds = ClusterList;
        }
        if (clusterIds.contains(Constant.Tags.CULSTER_IDS_ALL_VALUE)){
            clusterIds=this.findAllIds.getClusterIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> finalResult = clusterIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Cluster.QUERY_CLUSTER_CPU_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas cpu_usage is fail : " + e);
                throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.CLUSTER_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setValue(rate.getRate());
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getDomainCpu(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> finalResult = domainIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_CPU_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas cluster_cpu is fail : " + e);
                throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.DOMAIN_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setValue(rate.getRate());
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getAllCpu(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> dataValueAndTagsDTOList = new CopyOnWriteArrayList<>();
        List<HostDTO> hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                username, password, CasUriConstants.Host.HOST_ALL_INFO, new ParameterizedTypeReference<List<HostDTO>>() {
                });

        List<DataValueAndTagsDTO> hostCpu = null;
        if (CollUtil.isNotEmpty(hostDTOList)) {
            List<String> hostIds = hostDTOList.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
            hostCpu = this.getHostCpu(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        }

        List<ClusterDTO> clusterDTOS = this.casRestConnection.get(platform, host, protocol, port,
                username, password, CasUriConstants.Cluster.CLUSTER_QUERY_ALL, new ParameterizedTypeReference<List<ClusterDTO>>() {
                });
        List<DataValueAndTagsDTO> clusterCpu = null;
        if (CollUtil.isNotEmpty(clusterDTOS)) {
            List<String> clusterIds = clusterDTOS.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
            clusterCpu = this.getClusterCpu(platform, clusterIds, host, protocol, port, username, password, tags, resourceId);
        }
        List<DomainDTO> domainDTOList = this.casRestConnection.get(platform, host, protocol, port,
                username, password, CasUriConstants.Domain.DOMAIN_QUERY_ALL, new ParameterizedTypeReference<List<DomainDTO>>() {
                });
        List<DataValueAndTagsDTO> domainCpu = null;
        if (CollUtil.isNotEmpty(domainDTOList)) {
            List<String> domainIds = domainDTOList.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
            domainCpu = this.getDomainCpu(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        }
        dataValueAndTagsDTOList.addAll(hostCpu);
        dataValueAndTagsDTOList.addAll(clusterCpu);
        dataValueAndTagsDTOList.addAll(domainCpu);
        return dataValueAndTagsDTOList;
    }


    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cpu_usage;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }

}
