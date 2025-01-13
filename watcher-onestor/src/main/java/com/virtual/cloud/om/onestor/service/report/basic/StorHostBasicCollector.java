package com.virtual.cloud.om.onestor.service.report.basic;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.onestor.OneStorRoleEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.*;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import jdk.nashorn.internal.runtime.arrays.ArrayData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OneStor主机基本信息采集器
 * */

@Service
@RequiredArgsConstructor
@Slf4j
public class StorHostBasicCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<StorHostBasicReportDTO> infos = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        //OneStor管理节点调用接口后获取集群内的所有相关信息
        try {
            String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
            //获取主机角色信息
            String url = String.format(OnestoreUriConstants.Host.ROLE_INFO, clusterId, "");
            OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
            List<OneStorHostRoleInfoDTO> oneStorHostRoleInfoDTOList = mapper.convertValue(data.get("hosts"), new TypeReference<List<OneStorHostRoleInfoDTO>>(){});
            //获取存储节点信息
            url = String.format(OnestoreUriConstants.Host.STOR_INFO, clusterId, Constant.Version.ONESTOR_VERSION_NUM);
            oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            data = (LinkedHashMap)oneStorRestResult.getData();
            String clusterName = mapper.convertValue(data.get("cluster_name"), new TypeReference<String>(){});
            List<OneStorHostStorInfoDTO> oneStorHostStorInfoDTOList = mapper.convertValue(data.get("hosts"), new TypeReference<List<OneStorHostStorInfoDTO>>(){});
            Map<String, OneStorHostStorInfoDTO> oneStorHostStorInfoDTOMap = oneStorHostStorInfoDTOList.stream().collect(Collectors.toMap(OneStorHostStorInfoDTO::getHost_name, OneStorHostStorInfoDTO -> OneStorHostStorInfoDTO));
            //获取监控节点信息
            url = String.format(OnestoreUriConstants.Host.MONITOR_INFO, clusterId);
            oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            data = (LinkedHashMap)oneStorRestResult.getData();
            List<OneStorHostMonitorInfoDTO> oneStorHostMonitorInfoDTOList = mapper.convertValue(data.get("mons"), new TypeReference<List<OneStorHostMonitorInfoDTO>>(){});
            Map<String, OneStorHostMonitorInfoDTO> oneStorHostMonitorInfoDTOMap = oneStorHostMonitorInfoDTOList.stream().collect(Collectors.toMap(OneStorHostMonitorInfoDTO::getHost_name, OneStorHostMonitorInfoDTO -> OneStorHostMonitorInfoDTO));
            //NAS节点信息
            url = String.format(OnestoreUriConstants.Host.NAS_INFO, clusterId);
            oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            List dataList = (List)oneStorRestResult.getData();
            List<OneStorHostNasInfoDTO> oneStorHostNasInfoDTOList = mapper.convertValue(dataList, new TypeReference<List<OneStorHostNasInfoDTO>>(){});
            Map<String, OneStorHostNasInfoDTO> oneStorHostNasInfoDTOMap = oneStorHostNasInfoDTOList.stream().collect(Collectors.toMap(OneStorHostNasInfoDTO::getHost_name, OneStorHostNasInfoDTO -> OneStorHostNasInfoDTO));
            //MDS节点信息
            url = String.format(OnestoreUriConstants.Host.MDS_INFO, clusterId);
            oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            dataList = (List)oneStorRestResult.getData();
            List<OneStorHostMdsInfoDTO> oneStorHostMdsInfoDTOList = mapper.convertValue(dataList, new TypeReference<List<OneStorHostMdsInfoDTO>>(){});
            Map<String, OneStorHostMdsInfoDTO> oneStorHostMdsInfoDTOMap = oneStorHostMdsInfoDTOList.stream().collect(Collectors.toMap(OneStorHostMdsInfoDTO::getName, OneStorHostMdsInfoDTO -> OneStorHostMdsInfoDTO));
            //根据角色装载信息
            setInfo(infos, clusterId, clusterName, oneStorHostRoleInfoDTOList, oneStorHostStorInfoDTOMap, oneStorHostMonitorInfoDTOMap, oneStorHostNasInfoDTOMap, oneStorHostMdsInfoDTOMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_host_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

    private void setInfo(List<StorHostBasicReportDTO> infos,
                         String clusterId,
                         String clusterName,
                         List<OneStorHostRoleInfoDTO> oneStorHostRoleInfoDTOList,
                         Map<String, OneStorHostStorInfoDTO> oneStorHostStorInfoDTOMap,
                         Map<String, OneStorHostMonitorInfoDTO> oneStorHostMonitorInfoDTOMap,
                         Map<String, OneStorHostNasInfoDTO> oneStorHostNasInfoDTOMap,
                         Map<String, OneStorHostMdsInfoDTO> oneStorHostMdsInfoDTOMap
                         ){
        oneStorHostRoleInfoDTOList.forEach(hostRole -> {
            StorHostBasicReportDTO reportDTO = new StorHostBasicReportDTO();
            Integer role = hostRole.getRole();
            String hostName = hostRole.getName();

            reportDTO.setRole(role);
            reportDTO.setHost_name(hostName);
            reportDTO.setFs_id(clusterId);

            Set<OneStorRoleEnum> roleList = OneStorRoleEnum.getRoleList(role);
            if(roleList.contains(OneStorRoleEnum.ROLE_STOR)){
                OneStorHostStorInfoDTO hostStorInfoDTO = oneStorHostStorInfoDTOMap.get(hostName);
                BeanUtil.copyProperties(hostStorInfoDTO, reportDTO, new String[]{"maintain_mode", "role", "hostName"});
                if(Objects.isNull(reportDTO.getPublic_ip())){
                    reportDTO.setPublic_ip(hostStorInfoDTO.getManage_ip());
                }
                reportDTO.setCluster_name(clusterName)
                        .setDisk_status_ok_num(hostStorInfoDTO.getDisk_status().getOk())
                        .setDisk_status_fail_num(hostStorInfoDTO.getDisk_status().getFail())
                        .setDisk_status_total_num(hostStorInfoDTO.getDisk_status().getTotal())
                        .setMem_total_bytes(hostStorInfoDTO.getMem().getTotal_bytes())
                        .setMem_avail_bytes(hostStorInfoDTO.getMem().getAvail_bytes())
                        .setMem_used_pct(hostStorInfoDTO.getMem().getUsed_bytes())
                        .setMem_avail_bytes(hostStorInfoDTO.getMem().getAvail_bytes())
                        .setCpu_percent(hostStorInfoDTO.getCpu())
                        .setTotal_bytes(hostStorInfoDTO.getCapacity().getTotal_bytes())
                        .setAvail_bytes(hostStorInfoDTO.getCapacity().getAvail_bytes())
                        .setUsed_bytes(hostStorInfoDTO.getCapacity().getUsed_bytes())
                        .setUsed_pct(hostStorInfoDTO.getCapacity().getUsed_pct())
                        .setMaintain_mode(hostStorInfoDTO.getMaintain_mode());
            }
            if(roleList.contains(OneStorRoleEnum.ROLE_MON)){
                OneStorHostMonitorInfoDTO hostMonitorInfoDTO = oneStorHostMonitorInfoDTOMap.get(hostName);
                reportDTO.setMem_avail_bytes(hostMonitorInfoDTO.getMem().getAvail_bytes())
                        .setMem_used_bytes(hostMonitorInfoDTO.getMem().getUsed_bytes())
                        .setMem_used_pct(hostMonitorInfoDTO.getMem().getUsed_pct())
                        .setMem_total_bytes(hostMonitorInfoDTO.getMem().getTotal_bytes())
                        .setHost_status(hostMonitorInfoDTO.getHost_status())
                        .setCpu_percent(hostMonitorInfoDTO.getCpu())
                        .setNodepool_name(hostMonitorInfoDTO.getNodepool_name())
                        .setPublic_ip(hostMonitorInfoDTO.getManage_ip())
                        .setCluster_ip(Objects.nonNull(hostMonitorInfoDTO.getCluster_ip()) ? hostMonitorInfoDTO.getCluster_ip() : hostMonitorInfoDTO.getManage_ip())
                        .setManage_ip(hostMonitorInfoDTO.getManage_ip())
                        .setMon_status(hostMonitorInfoDTO.getMon_status())
                        .setMds_status(hostMonitorInfoDTO.getMon_status());
            }
            if(roleList.contains(OneStorRoleEnum.ROLE_NAS)){
                OneStorHostNasInfoDTO oneStorHostNasInfoDTO = oneStorHostNasInfoDTOMap.get(hostName);
                reportDTO.setNas_subhealthy(oneStorHostNasInfoDTO.getSubhealthy())
                        .setNas_healthy(oneStorHostNasInfoDTO.getHealthy())
                        .setMem_used_bytes(oneStorHostNasInfoDTO.getMemory().get(0) == null ? 0 : oneStorHostNasInfoDTO.getMemory().get(0))
                        .setMem_total_bytes(oneStorHostNasInfoDTO.getMemory().get(1) == null ? 0 : oneStorHostNasInfoDTO.getMemory().get(1))
                        .setMem_used_pct(oneStorHostNasInfoDTO.getMemory().get(2) == null ? 0 : oneStorHostNasInfoDTO.getMemory().get(2))
                        .setManage_ip(oneStorHostNasInfoDTO.getHost_address())
                        .setManage_ip_slave(oneStorHostNasInfoDTO.getHost_address_slave())
                        .setCpu_percent(oneStorHostNasInfoDTO.getCpu());

            }
            if(roleList.contains(OneStorRoleEnum.ROLE_MDS)){
                OneStorHostMdsInfoDTO oneStorHostMdsInfoDTO = oneStorHostMdsInfoDTOMap.get(hostName);
                reportDTO.setManage_ip(oneStorHostMdsInfoDTO.getIp_addr())
                        .setManage_ip_slave(oneStorHostMdsInfoDTO.getIp_addr_slave())
                        .setMds_status(oneStorHostMdsInfoDTO.getStatus())
                ;
            }


            infos.add(reportDTO);
        });
    }
}
