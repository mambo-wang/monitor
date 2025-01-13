package com.virtual.cloud.om.onestor.service.clusterBasic;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.OneStorClusterMonitorEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRenderResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterCapacityDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterFlowDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/8/24 21:11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterCapacityCollector extends DataReportCollector {
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

        String url = OnestoreUriConstants.Cluster.STOR_CLUSTER_MONITOR
                + Constant.TARGET + OneStorClusterMonitorEnum.big_cluster_space_total.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_space_used.getValue();

        List<OneStorRenderResult> oneStorRenderResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<List<OneStorRenderResult>>() {
        }).getBody();
        Map<String, List<List>> stringListMap = oneStorRenderResult.stream().collect(Collectors.toMap(OneStorRenderResult::getTarget, OneStorRenderResult::getDatapoints));
        List<List> space_total_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_space_total.getValue());
        List<List> space_used_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_space_used.getValue());

        Double space_total_value=(Double)space_total_list.get(space_total_list.size() - 1).get(0);
        Integer space_total_time=(Integer)space_total_list.get(space_total_list.size() - 1).get(1);
        Double space_used_value=(Double)space_used_list.get(space_used_list.size() - 1).get(0);
        StorClusterCapacityDTO storClusterCapacityDTO =new StorClusterCapacityDTO();
        storClusterCapacityDTO.setClusterName(clusterId.getName());
        storClusterCapacityDTO.setTotal(space_total_value);
        storClusterCapacityDTO.setUsed(space_used_value);
        Long time = Long.valueOf(space_total_time) * 1000;
        String formatFullDateTime = Utils.formatFullDateTime(time);
        storClusterCapacityDTO.setUpdate_time(formatFullDateTime);
        DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(storClusterCapacityDTO);
        log.info("stor_cluster_capacity : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_capacity;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
