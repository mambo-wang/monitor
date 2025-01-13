package com.virtual.cloud.om.onestor.service.clusterBasic;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterCapacityBasicResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorClusterRbdCapacityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/12/30 14:10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterRbdCapacityCollector extends DataReportCollector {
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
        String url = String.format(OnestoreUriConstants.Cluster.STOR_CLUSTER_CAPACITY_BASIC, clusterId.getId());
        StorClusterCapacityBasicResult oneStorRestResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<StorClusterCapacityBasicResult>() {
        }).getBody();

        if (Objects.isNull(oneStorRestResult)){
            return Collections.emptyList();
        }
        StorClusterRbdCapacityDTO storClusterRbdCapacityDTO=new StorClusterRbdCapacityDTO();
        storClusterRbdCapacityDTO.setFs_id(clusterId.getId());
        storClusterRbdCapacityDTO.setRbd_total_space(oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_total_space());
        storClusterRbdCapacityDTO.setRbd_avail_space(oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_avail_space());
        storClusterRbdCapacityDTO.setRbd_used_space(oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_used_space());
        storClusterRbdCapacityDTO.setRbd_flag(oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_flag());

        DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setValue(Lists.newArrayList(storClusterRbdCapacityDTO));
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_rbd_capacity;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
