package com.virtual.cloud.om.onestor.service;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtual.cloud.om.sdk.api.HostApi;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.utils.JsonUtils;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: w22798
 * @Date: 2022/5/19 14:51
 */
@Component
public class OnestorHostHandler implements HostApi {

    @Resource
    private OnestorRestConnection onestorRestConnection;

    @Override
    public SSHHost getHost(RestHost cvmHost, String endpoint) {
        //在OneStor中没有hostId这一说，所以endpoint直接用管理节点获子节点的IP地址。子节点的用户名与密码与管理节点一致。
        List<OneStorHostInfoDTO> oneStorRpcResultList = getOneStorHostInfoList(cvmHost);
        OneStorHostInfoDTO currentHost = oneStorRpcResultList.stream().filter(p -> p.getCluster_ip().equals(endpoint)).collect(Collectors.toList()).get(0);
        SSHHost sshHost = SSHHost.newInstance(endpoint, cvmHost.getServerUsername(), cvmHost.getServerPassword(), 0L, currentHost.getName(), cvmHost.getResourceId(), cvmHost.getPlatform());

        return sshHost;
    }

    @Override
    public Set<String> queryHostIds(RestHost cvmHost) {
        try {
            List<String> hostIdsList = getOneStorHostInfoList(cvmHost).stream().map(OneStorHostInfoDTO::getCluster_ip).collect(Collectors.toList());
            return new HashSet<>(hostIdsList);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public ReportResourceEnum whoAreYou() {
        return ReportResourceEnum.onestor;
    }


    private List<OneStorHostInfoDTO> getOneStorHostInfoList(RestHost cvmHost){
        //获取HOST信息
        String url = String.format(OnestoreUriConstants.Cluster.CLUSTER_SERVER_INFO, "stor,mon,rgw,handy,nas,mds");

        OneStorRestResult oneStorRestResult = onestorRestConnection.get(cvmHost.getHost(), cvmHost.getProtocol(), cvmHost.getUsername(), cvmHost.getPassword(), cvmHost.getPort(), url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        ObjectMapper mapper = new ObjectMapper();
        List<OneStorHostInfoDTO> oneStorRpcResultList = mapper.convertValue(data.get("hosts"), new TypeReference<List<OneStorHostInfoDTO>>(){}) ;
        return oneStorRpcResultList;

    }
}
