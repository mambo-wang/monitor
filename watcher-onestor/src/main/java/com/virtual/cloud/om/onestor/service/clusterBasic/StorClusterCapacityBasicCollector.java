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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterCapacityBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterCapacityBasicResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/8/24 16:22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterCapacityBasicCollector extends DataReportCollector {
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
//        private Long total_bytes;
//        private Long total_valid_bytes;
//        private Long avail_bytes;
//        private Long avail_valid_bytes;
//        private Long used_bytes;
//        private Long used_valid_bytes;
//        private Long pools_used_bytes;
        StorClusterCapacityBasicDTO dto =new StorClusterCapacityBasicDTO();
        Long total_bytes = oneStorRestResult.getPool().getTotal_bytes();
        Long total_valid_bytes = oneStorRestResult.getPool().getTotal_valid_bytes();
        Long avail_bytes = oneStorRestResult.getPool().getAvail_bytes();
        Long avail_valid_bytes = oneStorRestResult.getPool().getAvail_valid_bytes();
        Long used_bytes = oneStorRestResult.getPool().getUsed_bytes();
        Long used_valid_bytes = oneStorRestResult.getPool().getUsed_valid_bytes();
        Long pools_used_bytes = oneStorRestResult.getPool().getPools_used_bytes();
        dto.setTotal_bytes(total_bytes);
        dto.setTotal_valid_bytes(total_valid_bytes);
        dto.setAvail_bytes(avail_bytes);
        dto.setAvail_valid_bytes(avail_valid_bytes);
        dto.setUsed_bytes(used_bytes);
        dto.setUsed_valid_bytes(used_valid_bytes);
        dto.setPools_used_bytes(pools_used_bytes);
//        Double rbd_total_space = oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_total_space();
//        Double rbd_avail_space = oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_avail_space();
//        Double rbd_used_space = oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_used_space();
//        Boolean rbd_flag = oneStorRestResult.getPool().getCluster_capacity_detail().getRbd_flag();

        dto.setFs_id(clusterId.getId());
//        dto.setRbd_total_space(rbd_total_space);
////        dto.setRbd_avail_space(rbd_avail_space);
////        dto.setRbd_used_space(rbd_used_space);
////        dto.setRbd_flag(rbd_flag);
        DataValueAndTagsDTO dataValueAndTagsDTO=new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(Lists.newArrayList(dto));
        log.info("stor_cluster_capacity_basic :{}",dataValueAndTagsDTO);
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_capacity_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
