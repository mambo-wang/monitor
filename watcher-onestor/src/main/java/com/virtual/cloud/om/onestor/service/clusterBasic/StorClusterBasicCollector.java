package com.virtual.cloud.om.onestor.service.clusterBasic;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterBasicGetDTO;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/8/23 14:08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterBasicCollector extends DataReportCollector {

    private final OnestorRestConnection onestorRestConnection;
    @Resource
    private StorUtils storUtils;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        StorCluserIdDTO clusterId = storUtils.getClusterId(platform, host, protocol, port, username, password, tags, resourceId);
        if (Objects.isNull(clusterId.getId())){
            log.info("ONESTOR StorCluserIdDTO is empty host : {}",host);
            return Collections.emptyList();
        }
        String url = String.format(OnestoreUriConstants.Cluster.STOR_CLUSTER_BASIC, clusterId.getId());
        OneStorRestResult oneStorRestResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>() {
        }).getBody();
        if (Objects.isNull(oneStorRestResult)){
            return Collections.emptyList();
        }
        DataValueAndTagsDTO dataValueAndTagsDTO=new DataValueAndTagsDTO();
        StorClusterBasicDTO storClusterBasicDTO=new StorClusterBasicDTO();
        LinkedHashMap data = (LinkedHashMap) oneStorRestResult.getData();
        String client_name = data.get("client_name").toString();
        String unistor_cluster_name = data.get("unistor_cluster_name").toString();
        storClusterBasicDTO.setCluster_name(unistor_cluster_name);
        storClusterBasicDTO.setClient_name(client_name);
        storClusterBasicDTO.setFs_id(clusterId.getId());
        storClusterBasicDTO.setUpdate_time(clusterId.getUpdateTime());
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(Lists.newArrayList(storClusterBasicDTO));
        log.info("ONESTOR StorCluserIdDTO collector finish result : {}",storClusterBasicDTO);
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
