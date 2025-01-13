package com.virtual.cloud.om.onestor.service.report.monitor.node;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.OneStorClusterMonitorEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorNodePoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.node.OneStorNodeIOPSMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 节点池 IOPS监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodeIOPSMonitorCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        //获取所有的节点池
        String nodePoolUrl = String.format(OnestoreUriConstants.NodePool.BASIC_INFO_NODE_POOL, onestorRestConnection.getClusterId(host, protocol, username, password, port));
        OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, nodePoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        List<StorNodePoolBasicDTO> nodePoolDTOList = mapper.convertValue(data.get("nodepool_list"), new TypeReference<List<StorNodePoolBasicDTO>>(){});
        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = nodePoolDTOList.parallelStream().map(nodePool -> {
            String nodePoolName = nodePool.getNodepool_name();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.iops_read.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.iops_write.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.ops_recover.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.ops_read.getValue(), nodePoolName));
            url.append(appendTarget(OneStorNodePoolMonitorTargetEnum.ops_write.getValue(), nodePoolName))
                    .append(Constant.TARGET+ OneStorClusterMonitorEnum.big_cluster_pool_all_iops_all.getValue()
                            +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_fs_ops_total.getValue());

            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorNodeIOPSMonitorReportDTO reportDTO = new OneStorNodeIOPSMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setPoolName(nodePoolName)
                    .setAllIops(getData(oneStorMonitorDTOMap,OneStorClusterMonitorEnum.big_cluster_pool_all_iops_all.getValue(),""))
                    .setFsTotalOps(getData(oneStorMonitorDTOMap,OneStorClusterMonitorEnum.big_cluster_fs_ops_total.getValue(),""))
                    .setIopsRead(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.iops_read.getValue(), nodePoolName))
                    .setIopsWrite(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.iops_write.getValue(), nodePoolName))
                    .setRecoverOps(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.ops_recover.getValue(), nodePoolName))
                    .setOpsRead(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.ops_read.getValue(), nodePoolName))
                    .setOpsWrite(getData(oneStorMonitorDTOMap, OneStorNodePoolMonitorTargetEnum.ops_write.getValue(), nodePoolName))
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
        return DataReportTypeByMetricEnum.node_iops;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
