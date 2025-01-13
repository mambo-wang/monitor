package com.virtual.cloud.om.onestor.service.report.monitor.host;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorDiskPoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorHostMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorNodePoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.OneStorHostRoleInfoDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorHostBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.host.OneStorHostIOPSMonitorReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor 主机 IOPS监控信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostIOPSMonitorCollector extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        ObjectMapper mapper = new ObjectMapper();
        //获取所有的主机
        String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
        String hostUrl = String.format(OnestoreUriConstants.Host.ROLE_INFO, clusterId, "");
        OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, hostUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        List<OneStorHostRoleInfoDTO> basicDTOList = mapper.convertValue(data.get("hosts"), new TypeReference<List<OneStorHostRoleInfoDTO>>(){});
        //获取每个节点池监控数据
        List<DataValueAndTagsDTO> finalResult = basicDTOList.parallelStream().map(basicDTO -> {
            String hostName = basicDTO.getName();
            StringBuffer url = new StringBuffer(OnestoreUriConstants.Monitor.MONITOR_BY_TARGET);
            url.append(appendTarget(OneStorHostMonitorTargetEnum.iops_read.getValue(), hostName));
            url.append(appendTarget(OneStorHostMonitorTargetEnum.iops_write.getValue(), hostName));
            url.append(appendTarget(OneStorHostMonitorTargetEnum.ops_read.getValue(), hostName));
            url.append(appendTarget(OneStorHostMonitorTargetEnum.ops_write.getValue(), hostName));

            List<OneStorMonitorDTO> oneStorMonitorDTOList = onestorRestConnection.get(host, protocol, username, password, port, url.toString(), new ParameterizedTypeReference<List<OneStorMonitorDTO>>() {
            }).getBody();
            Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap = oneStorMonitorDTOList.stream().collect(Collectors.toMap(OneStorMonitorDTO::getTarget, p -> p));
            OneStorHostIOPSMonitorReportDTO reportDTO = new OneStorHostIOPSMonitorReportDTO();
            Date currentDate = getTime(oneStorMonitorDTOMap);
            reportDTO.setHostName(hostName)
                    .setIopsRead(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.iops_read.getValue(), hostName))
                    .setIopsWrite(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.iops_write.getValue(), hostName))
                    .setOpsRead(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.ops_read.getValue(), hostName))
                    .setRecoverOps(getData(oneStorMonitorDTOMap, OneStorHostMonitorTargetEnum.ops_write.getValue(), hostName))
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
        return DataReportTypeByMetricEnum.host_iops;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
