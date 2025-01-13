package com.virtual.cloud.om.onestor.service.clusterBasic;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.*;
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
 * @Date:2022/8/24 16:42
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterFsCapacityCollector extends DataReportCollector {
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

        Double fs_total_space = oneStorRestResult.getPool().getCluster_capacity_detail().getFs_total_space();
        Double fs_avail_space = oneStorRestResult.getPool().getCluster_capacity_detail().getFs_avail_space();
        Double fs_used_space = oneStorRestResult.getPool().getCluster_capacity_detail().getFs_used_space();
        Boolean fs_flag = oneStorRestResult.getPool().getCluster_capacity_detail().getFs_flag();
        StorClusterFsCapacityDTO dto =new StorClusterFsCapacityDTO();
        dto.setFs_id(clusterId.getId());
        dto.setFs_total_space(fs_total_space);
        dto.setFs_avail_space(fs_avail_space);
        dto.setFs_used_space(fs_used_space);
        dto.setFs_flag(fs_flag);
        DataValueAndTagsDTO dataValueAndTagsDTO=new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(Lists.newArrayList(dto));
        log.info("stor_cluster_fs_capacity : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_fs_capacity;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
