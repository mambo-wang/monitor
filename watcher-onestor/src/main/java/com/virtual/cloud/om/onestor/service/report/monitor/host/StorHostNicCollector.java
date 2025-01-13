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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorHostNicDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/11/24 18:01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorHostNicCollector extends DataReportCollector implements IOnestorMonitorCollector {

    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS =new CopyOnWriteArrayList<>();

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
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_rx_byte.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_tx_byte.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_rx_packets.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_rx_drop.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_rx_errors.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_tx_packets.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_tx_drop.getValue(),role.getName())
                        + Constant.TARGET +String.format(OneStorClusterMonitorEnum.network_all_tx_errors.getValue(),role.getName());
                List<OneStorRenderResult> oneStorRenderResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<List<OneStorRenderResult>>() {
                }).getBody();
                Map<String, List<List>> stringListMap = oneStorRenderResult.stream().collect(Collectors.toMap(OneStorRenderResult::getTarget, OneStorRenderResult::getDatapoints));
                List<List> all_rx_byte_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_rx_byte.getValue(), role.getName()));
                List<List> all_tx_byte_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_tx_byte.getValue(), role.getName()));
                List<List> all_rx_packets_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_rx_packets.getValue(), role.getName()));
                List<List> all_rx_drop_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_rx_drop.getValue(), role.getName()));
                List<List> all_rx_errors_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_rx_errors.getValue(), role.getName()));
                List<List> all_tx_packets_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_tx_packets.getValue(), role.getName()));
                List<List> all_tx_drop_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_tx_drop.getValue(), role.getName()));
                List<List> all_tx_errors_list = stringListMap.get(String.format(OneStorClusterMonitorEnum.network_all_tx_errors.getValue(), role.getName()));
                Double all_rx_byte=0.0;
                if (CollectionUtil.isNotEmpty(all_rx_byte_list)){
                    all_rx_byte=(Double)all_rx_byte_list.get(all_rx_byte_list.size() - 1).get(0);
                }
                Double all_tx_byte=0.0;
                if (CollectionUtil.isNotEmpty(all_tx_byte_list)){
                    all_tx_byte=(Double)all_tx_byte_list.get(all_tx_byte_list.size() - 1).get(0);
                }
                Double all_rx_packets=0.0;
                if (CollectionUtil.isNotEmpty(all_rx_packets_list)){
                    all_rx_packets=(Double)all_rx_packets_list.get(all_rx_packets_list.size() - 1).get(0);
                }
                Double all_rx_drop=0.0;
                if (CollectionUtil.isNotEmpty(all_rx_drop_list)){
                    all_rx_drop=(Double)all_rx_drop_list.get(all_rx_drop_list.size() - 1).get(0);
                }
                Double all_rx_errors=0.0;
                if (CollectionUtil.isNotEmpty(all_rx_errors_list)){
                    all_rx_errors=(Double)all_rx_errors_list.get(all_rx_errors_list.size() - 1).get(0);
                }
                Double all_tx_packets=0.0;
                if (CollectionUtil.isNotEmpty(all_tx_packets_list)){
                    all_tx_packets=(Double)all_tx_packets_list.get(all_tx_packets_list.size() - 1).get(0);
                }
                Double all_tx_drop=0.0;
                if (CollectionUtil.isNotEmpty(all_tx_drop_list)){
                    all_tx_drop=(Double)all_tx_drop_list.get(all_tx_drop_list.size() - 1).get(0);
                }
                Double all_tx_errors=0.0;
                if (CollectionUtil.isNotEmpty(all_tx_errors_list)){
                    all_tx_errors=(Double)all_tx_errors_list.get(all_tx_errors_list.size() - 1).get(0);
                }
                DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
                StorHostNicDTO storHostNicDTO=new StorHostNicDTO();
                storHostNicDTO.setHostName(host_name);
                storHostNicDTO.setInputErrPackets(all_rx_errors);
                storHostNicDTO.setInputPackets(all_rx_packets);
                storHostNicDTO.setInputPacketsDropped(all_rx_drop);
                storHostNicDTO.setInputPacketStats(all_rx_byte);
                storHostNicDTO.setOutputErrPackets(all_tx_errors);
                storHostNicDTO.setOutputPackets(all_tx_packets);
                storHostNicDTO.setOutputPacketsDropped(all_tx_drop);
                storHostNicDTO.setOutputPacketStats(all_tx_byte);
                dataValueAndTagsDTO.setValue(storHostNicDTO);
                dataValueAndTagsDTO.setTags(tags);
                dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
                dataValueAndTagsDTOS.add(dataValueAndTagsDTO);
        });

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("result: {}",dataValueAndTagsDTOS);
        return dataValueAndTagsDTOS;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_host_nic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
