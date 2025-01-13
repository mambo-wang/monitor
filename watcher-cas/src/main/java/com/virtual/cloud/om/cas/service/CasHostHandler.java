package com.virtual.cloud.om.cas.service;

import com.virtual.cloud.om.sdk.api.HostApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.HostInfo;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.RsHost;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: w22798
 * @Date: 2022/5/13 9:38
 */
@Component
public class CasHostHandler implements HostApi {

    @Resource
    private CasRestConnection casRestConnection;

    @Override
    public SSHHost getHost(RestHost cvmHost, String endpoint) {
        if(StringUtils.equalsIgnoreCase(endpoint, "0")){
            SSHHost sshHost = SSHHost.newInstance(cvmHost.getHost(), cvmHost.getServerUsername(), cvmHost.getServerPassword(), 0L, "管理平台", cvmHost.getResourceId(), cvmHost.getPlatform());
            return sshHost;
        }
        String url = String.format(CasUriConstants.Host.HOST_BASIC_INFO, Long.parseLong(endpoint));
        HostInfo hostInfo = casRestConnection.get(cvmHost.getPlatform(), cvmHost.getHost(), cvmHost.getProtocol(), cvmHost.getPort(), cvmHost.getUsername(), cvmHost.getPassword(), url, new ParameterizedTypeReference<HostInfo>() {
        });
        SSHHost sshHost = SSHHost.newInstance(hostInfo.getIp(), hostInfo.getUser(), hostInfo.getPwd(), hostInfo.getId(), hostInfo.getName(), cvmHost.getResourceId(), cvmHost.getPlatform());
        return sshHost;
    }

    @Override
    public Set<String> queryHostIds(RestHost restHost) {
        String url = CasUriConstants.Host.HOST_BASIC_INFO_ALL;
        try {
            List<RsHost> rsHosts = casRestConnection.get(restHost.getPlatform(), restHost.getHost(), restHost.getProtocol(), restHost.getPort(), restHost.getUsername(), restHost.getPassword(), url, new ParameterizedTypeReference<List<RsHost>>() {
            });
            return rsHosts.stream().map(rsHost -> rsHost.getId().toString()).collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public ReportResourceEnum whoAreYou() {
        return ReportResourceEnum.cas;
    }
}
