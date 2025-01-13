package com.virtual.cloud.om.onestor.service.report.monitor.node;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorNodePoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node.OneStorNodeFlowMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 节点池 Flow监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodeFlowMonitorCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        //获取所有的节点池
        String nodePoolUrl = String.format(OnestoreUriConstants.NodePool.BASIC_INFO_NODE_POOL, onestorRestConnection.getClusterId(host, protocol, username, password, port));
        OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, nodePoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        List<StorNodePoolBasicDTO>  nodePoolDTOList = mapper.convertValue(data.get("nodepool_list"), new TypeReference<List<StorNodePoolBasicDTO>>(){});
        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = nodePoolDTOList.parallelStream().map(nodePool -> {
            String nodePoolName = nodePool.getNodepool_name();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.storage_read_flow.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.storage_write_flow.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.storage_recover_flow.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.fs_read_flow.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.fs_write_flow.getValue(), nodePoolName));

            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorNodeFlowMonitorReportDTO reportDTO = new OneStorNodeFlowMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setPoolName(nodePoolName)
                    .setStorageReadFlow(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.storage_read_flow.getValue(), nodePoolName))
                    .setStorageWriteFlow(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.storage_write_flow.getValue(), nodePoolName))
                    .setStorageRecoverFlow(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.storage_recover_flow.getValue(), nodePoolName))
                    .setFsReadFlow(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.fs_read_flow.getValue(), nodePoolName))
                    .setFsWriteFlow(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.fs_write_flow.getValue(), nodePoolName))
                    ;
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(reportDTO);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(currentDate.getTime()));
            dataValueAndTagsDTO.setTags(tags);
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return finalResult;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.node_flow;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
