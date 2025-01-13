package com.virtual.cloud.om.onestor.service.report.monitor.host;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.OneStorHostTypeEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorDiskPoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorHostMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorHostRoleDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorHostBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host.OneStorHostDiskDelayMonitorReportDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host.OneStorHostDiskLoadMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 主机 disk_load监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostDiskloadCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        //获取所有的存储节点主机
        List<StorHostRoleDTO> allHostRoleDTOList = getAllHostRoleDTO(host, protocol, port, username, password);
        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = allHostRoleDTOList.parallelStream().map(roleHostDTO -> {
            String hostName = roleHostDTO.getName();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorHostMonitorTargetEnum.util_avg.getValue(), hostName));
            url.append(appendTarget(OneStorHostMonitorTargetEnum.util_max.getValue(), hostName));
            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorHostDiskLoadMonitorReportDTO reportDTO = new OneStorHostDiskLoadMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setHostName(hostName)
                    .setUtilAvg(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.util_avg.getValue(), hostName))
                    .setUtilMax(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.util_max.getValue(), hostName))
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
        return DataReportTypeByMetricEnum.host_disk_load;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

    //获取所有的存储节点
    private List<StorHostRoleDTO> getAllHostRoleDTO(String host, String protocol, Integer port, String username, String password){
        ObjectMapper mapper = new ObjectMapper();
        String url = String.format(OnestoreUriConstants.Host.GET_HOSTS_BY_ROLES, OneStorHostTypeEnum.stor.name());
        OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        List<StorHostRoleDTO> storHostRoleDTOList = mapper.convertValue(data.get("hosts"), new TypeReference<List<StorHostRoleDTO>>(){});
        return storHostRoleDTOList;
    }

}
