package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollUtil;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ClusterDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.DomainDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/6/17 14:02
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FindAllIds {


    private final CasRestConnection casRestConnection;

    public List<String> getHostIds(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<HostDTO> hostDTOList = null;
        try {
            hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, CasUriConstants.Host.HOST_ALL_INFO, new ParameterizedTypeReference<List<HostDTO>>() {
                    });
        } catch (Exception e) {
            log.error("FINDALLID host ip {}, {}, error reason is :{}",host,CasUriConstants.Host.HOST_ALL_INFO,e.getMessage());
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, CasUriConstants.Host.HOST_ALL_INFO,e.getMessage());
        }
        if (CollUtil.isEmpty(hostDTOList)) {
            return Collections.emptyList();
        }
        List<String> hostIds = hostDTOList.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        return hostIds;
    }

    public List<String> getDomainIds(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DomainDTO> domainDTOList = null;
        try {
            domainDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, CasUriConstants.Domain.DOMAIN_QUERY_ALL, new ParameterizedTypeReference<List<DomainDTO>>() {
                    });
        } catch (Exception e) {
            log.error("FINDALLID domain ip {}, {}, error reason is :{}",host,CasUriConstants.Domain.DOMAIN_QUERY_ALL,e.getMessage());
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION,  CasUriConstants.Domain.DOMAIN_QUERY_ALL ,e.getMessage());
        }
        if (CollUtil.isEmpty(domainDTOList)) {
            return Collections.emptyList();
        }
        List<String> domainIds = domainDTOList.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        return domainIds;
    }

    public List<String> getClusterIds(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<ClusterDTO> clusterDTOS = null;
        try {
            clusterDTOS = this.casRestConnection.get(platform, host, protocol, port,
                    username, password,
                    CasUriConstants.Cluster.CLUSTER_QUERY_ALL, new ParameterizedTypeReference<List<ClusterDTO>>() {
                    });
        } catch (Exception e) {
            log.error("FINDALLID cluster ip {}, {}, error reason is :{}",host,CasUriConstants.Cluster.CLUSTER_QUERY_ALL,e.getMessage());
           throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION,CasUriConstants.Cluster.CLUSTER_QUERY_ALL,e.getMessage());
        }
        if (CollUtil.isEmpty(clusterDTOS)) {
            return Collections.emptyList();
        }
        List<String> clusterIds = clusterDTOS.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        return clusterIds;

    }

}
