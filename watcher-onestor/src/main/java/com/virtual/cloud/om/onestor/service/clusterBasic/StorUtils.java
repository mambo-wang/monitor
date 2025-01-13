package com.virtual.cloud.om.onestor.service.clusterBasic;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/8/23 14:37
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorUtils {
    @Resource
    private OnestorRestConnection onestorRestConnection;

    public StorCluserIdDTO getClusterId(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {

        OneStorRestResult oneStorRestResult = this.onestorRestConnection.get(host, protocol, username, password, port, OnestoreUriConstants.Cluster.ONESTOR_CLUSTER_ID, new ParameterizedTypeReference<OneStorRestResult>() {
        }).getBody();
        if (Objects.isNull(oneStorRestResult)){
            return new StorCluserIdDTO();
        }
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        String id = data.get("id").toString();
//        String name = data.get("name").toString();
        String updateTime = data.get("update_time").toString();
        String url = String.format(OnestoreUriConstants.Cluster.STOR_CLUSTER_BASIC, id);
        OneStorRestResult oneStorRestResultName = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>() {
        }).getBody();
        if (Objects.isNull(oneStorRestResultName)){
            return new StorCluserIdDTO();
        }
        LinkedHashMap dataName = (LinkedHashMap) oneStorRestResultName.getData();
        String unistor_cluster_name = dataName.get("unistor_cluster_name").toString();
        StorCluserIdDTO storCluserIdDTO=new StorCluserIdDTO();
        storCluserIdDTO.setId(id);
        storCluserIdDTO.setName(unistor_cluster_name);
        storCluserIdDTO.setUpdateTime(updateTime);
        return storCluserIdDTO;
    }
}
