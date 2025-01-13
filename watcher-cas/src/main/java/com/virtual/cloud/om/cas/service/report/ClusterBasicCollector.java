package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ClusterBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ClusterDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClusterBasicCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    public List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        //查询全部集群的信息
        List<ClusterBasicDTO> infos = Lists.newArrayList();
        List<ClusterDTO> clusterDTOS = null;
        try {
            clusterDTOS = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, CasUriConstants.Cluster.CLUSTER_QUERY_ALL, new ParameterizedTypeReference<List<ClusterDTO>>() {
                    });
            if (CollUtil.isEmpty(clusterDTOS)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, CasUriConstants.Cluster.CLUSTER_QUERY_ALL,e.getMessage());
        }
        clusterDTOS.forEach(cluster -> {
            ClusterBasicDTO basicDTO = new ClusterBasicDTO();
            basicDTO.setId(cluster.getId());
            basicDTO.setName(cluster.getName());
            basicDTO.setDescription(cluster.getDescription());
            basicDTO.setHa(cluster.getEnableHA());
            infos.add(basicDTO);
        });
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());

    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cluster_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
