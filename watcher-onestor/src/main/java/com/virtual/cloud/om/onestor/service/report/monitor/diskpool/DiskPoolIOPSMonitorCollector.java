package com.virtual.cloud.om.onestor.service.report.monitor.diskpool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorDiskPoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorDiskPoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.disk.OneStorDiskPoolIOPSMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 硬盘池 IOPS监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiskPoolIOPSMonitorCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        List<StorDiskPoolBasicDTO> basicDTOList = new ArrayList<>();
        //获取所有的硬盘池
        String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
        String nodePoolUrl = String.format(OnestoreUriConstants.NodePool.BASIC_INFO_NODE_POOL, clusterId);
        OneStorRestResult nodePoolRestResult = onestorRestConnection.get(host, protocol, username, password, port, nodePoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap nodePoolData = (LinkedHashMap)nodePoolRestResult.getData();
        List<StorNodePoolBasicDTO>  nodePoolDTOList = mapper.convertValue(nodePoolData.get("nodepool_list"), new TypeReference<List<StorNodePoolBasicDTO>>(){});
        nodePoolDTOList.forEach(nodePool -> {
            String diskPoolUrl = String.format(OnestoreUriConstants.DiskPool.BASIC_INFO_DISK_POOL, clusterId, nodePool.getNodepool_name());
            OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, diskPoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
            List<StorDiskPoolBasicDTO> dtoList = mapper.convertValue(data.get("diskpool_list"), new TypeReference<List<StorDiskPoolBasicDTO>>(){});
            basicDTOList.addAll(dtoList);
        });

        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = basicDTOList.parallelStream().map(basicDTO -> {
            String diskPoolName = basicDTO.getDiskpool_name();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorDiskPoolMonitorTargetEnum.iops_read.getValue(), diskPoolName));
            url.append(appendTarget(OneStorDiskPoolMonitorTargetEnum.iops_write.getValue(), diskPoolName));

            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorDiskPoolIOPSMonitorReportDTO reportDTO = new OneStorDiskPoolIOPSMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setDiskpoolName(diskPoolName)
                    .setIopsRead(getData(oneStorMonitorDTOMap, OneStorDiskPoolMonitorTargetEnum.iops_read.getValue(), diskPoolName))
                    .setIopsWrite(getData(oneStorMonitorDTOMap, OneStorDiskPoolMonitorTargetEnum.iops_write.getValue(), diskPoolName))
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
        return DataReportTypeByMetricEnum.diskpool_iops;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
