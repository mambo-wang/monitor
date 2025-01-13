package com.virtual.cloud.om.onestor.service.report.monitor.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorStorageMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorPoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.storage.OneStorStorageMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 存储池 IOPS监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageBandwidthMonitorCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        //获取所有的存储池
        String diskPoolUrl = String.format(OnestoreUriConstants.Pool.BASIC_INFO_POOL, onestorRestConnection.getClusterId(host, protocol, username, password, port));
        OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, diskPoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        List<StorPoolBasicDTO> basicDTOList = mapper.convertValue(data.get("pool_list"), new TypeReference<List<StorPoolBasicDTO>>(){});
        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = basicDTOList.parallelStream().map(basicDTO -> {
            String noodPoolName = basicDTO.getNodepool_name();
            String poolName = basicDTO.getPool_name();
            String id = basicDTO.getId().toString();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorStorageMonitorTargetEnum.storage_write_bw.getValue(), noodPoolName, id));
            url.append(appendTarget(OneStorStorageMonitorTargetEnum.storage_read_bw.getValue(), noodPoolName, id));
            url.append(appendTarget(OneStorStorageMonitorTargetEnum.onestor_pool_iops_all.getValue(), noodPoolName, id));
            url.append(appendTarget(OneStorStorageMonitorTargetEnum.onestor_pool_bw_all.getValue(), noodPoolName, id));
            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorStorageMonitorReportDTO reportDTO = new OneStorStorageMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setNodePoolName(poolName).
            setAllIops(getData(oneStorMonitorDTOMap,OneStorStorageMonitorTargetEnum.onestor_pool_iops_all.getValue(),noodPoolName,id))
                    .setAllBw(getData(oneStorMonitorDTOMap,OneStorStorageMonitorTargetEnum.onestor_pool_bw_all.getValue(),noodPoolName,id))
                    .setIopsRead(getData(oneStorMonitorDTOMap, OneStorStorageMonitorTargetEnum.storage_write_bw.getValue(), noodPoolName, poolName))
                    .setIopsWrite(getData(oneStorMonitorDTOMap, OneStorStorageMonitorTargetEnum.storage_read_bw.getValue(), noodPoolName, poolName))
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
        return DataReportTypeByMetricEnum.storage_bandwidth;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
