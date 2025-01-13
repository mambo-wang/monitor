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
import com.virtual.cloud.om.sdk.dto.dataReport.cas.Rates;
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
 * @Date:2022/5/24 15:14
 */

/**
 * 服务器下性能监控 主机内存利用率 2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostMemUsageCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.CLUSTER_IDS)) {
            finalResult = this.getClusterMem(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostMem(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainMem(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAllMem(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[mem_usage]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getHostMem(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
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
            String url = String.format(CasUriConstants.Host.QUERY_HOST_MEM_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas mem_usage is fail : " + e);
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(rate.getRate());
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getClusterMem(String platform, List<String> ClusterList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> clusterIds;
        if (CollectionUtil.isEmpty(ClusterList)) {
            clusterIds = getId(Constant.Tags.CLUSTER_IDS, tags);
        } else {
            clusterIds = ClusterList;
        }
        if (CollectionUtil.isEmpty(clusterIds)) {
            return Collections.emptyList();
        }
        if (clusterIds.contains(Constant.Tags.CULSTER_IDS_ALL_VALUE)){
            clusterIds=this.findAllIds.getClusterIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> finalResult = clusterIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Cluster.QUERY_CLUSTER_MEM_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas cluster_cpu is fail : " + e);
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(rate.getRate());
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.CLUSTER_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getDomainMem(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
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
        List<DataValueAndTagsDTO> finalResult = domainIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_MEM_USAGE, s);
            List<Rate> rateList = null;
            try {
                rateList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<Rate>>() {
                        });
            } catch (Exception e) {
                log.error("cas mem_usage is fail : " + e);
            }
            if (CollectionUtil.isEmpty(rateList)) {
                return null;
            }
            Rate rate = rateList.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(rate.getRate());
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.DOMAIN_ID, s);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(rate.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    private List<DataValueAndTagsDTO> getAllMem(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostMem = this.getHostMem(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<String> clusterIds = findAllIds.getClusterIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> clusterMem = this.getClusterMem(platform, clusterIds, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainMem = this.getDomainMem(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostMem.addAll(clusterMem);
        hostMem.addAll(domainMem);
        return hostMem;
    }


    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.mem_usage;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }

}
