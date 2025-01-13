package com.virtual.cloud.om.onestor.service.report.monitor.host;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.onestor.service.report.monitor.IOnestorMonitorCollector;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.OneStorClusterMonitorEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRenderResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.OneStorHostRoleInfoDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.OnestorAvgDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.OnestorDiskBasticUploadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/11/24 18:02
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorHostSysAvgLoadCollector  extends DataReportCollector implements IOnestorMonitorCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS =new CopyOnWriteArrayList<>();
        List<OnestorAvgDTO> infos = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        //OneStor管理节点调用接口后获取集群内的所有相关信息
        try {
            String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
            String hostUrl = String.format(OnestoreUriConstants.Host.ROLE_INFO, clusterId, "");
            OneStorRestResult oneStorRestResultHOST = onestorRestConnection.get(host, protocol, username, password, port, hostUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap data = (LinkedHashMap)oneStorRestResultHOST.getData();
            List<OneStorHostRoleInfoDTO> basicDTOList = mapper.convertValue(data.get("hosts"), new TypeReference<List<OneStorHostRoleInfoDTO>>(){});
            //获取所有的diskPool
            basicDTOList.forEach(role->{
                String host_name = role.getName();
                String url = OnestoreUriConstants.Host.HOST_GRAPHITE
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.server_loadavg_1.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.server_loadavg_5.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.server_loadavg_15.getValue(),role.getName());
                List<OneStorRenderResult> oneStorRenderResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<List<OneStorRenderResult>>() {
                }).getBody();
                Map<String, List<List>> stringListMap = oneStorRenderResult.stream().collect(Collectors.toMap(OneStorRenderResult::getTarget, OneStorRenderResult::getDatapoints));
                List<List> one_lists = stringListMap.get("servers."+host_name+".loadavg_1");
                List<List> five_lists = stringListMap.get("servers."+host_name+".loadavg_5");
                List<List> lists = stringListMap.get("servers."+host_name+".loadavg_15");
                Double one_result=0.0;
                if (CollectionUtil.isNotEmpty(one_lists)){
                    one_result=(Double)one_lists.get(one_lists.size() - 1).get(0);

                }
                Double five_result=0.0;
                if (CollectionUtil.isNotEmpty(five_lists)){
                    five_result=(Double)five_lists.get(five_lists.size() - 1).get(0);

                }
                Double lists_result=0.0;
                if (CollectionUtil.isNotEmpty(lists)){
                    lists_result=(Double)lists.get(lists.size() - 1).get(0);

                }
                OnestorAvgDTO onestorAvgDTO =new OnestorAvgDTO();
                DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
                onestorAvgDTO.setHostName(host_name);
                onestorAvgDTO.setOneMin(one_result);
                onestorAvgDTO.setFiveMin(five_result);
                onestorAvgDTO.setFifteenMin(lists_result);
                dataValueAndTagsDTO.setValue(onestorAvgDTO);
                dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
                dataValueAndTagsDTO.setTags(tags);
                dataValueAndTagsDTOS.add(dataValueAndTagsDTO);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("result: {}",vat);
        return dataValueAndTagsDTOS;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_host_sys_avg_load;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
