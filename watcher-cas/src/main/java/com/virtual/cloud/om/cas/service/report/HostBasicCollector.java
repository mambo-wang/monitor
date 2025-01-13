package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.UnitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostBasicCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<HostBasicDTO> infos = Lists.newArrayList();
        //查询主机列表
        String url = CasUriConstants.Host.HOST_ALL_INFO;
        List<HostDTO> hostDTOList;
        try {
            hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, url, new ParameterizedTypeReference<List<HostDTO>>() {
                    });
            if (CollUtil.isEmpty(hostDTOList)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        hostDTOList.forEach(hostDTO -> {
            HostBasicDTO basicDTO = new HostBasicDTO();
            Long hostId = hostDTO.getId();
            basicDTO.setId(hostId);
            basicDTO.setClusterId(hostDTO.getClusterId());
            basicDTO.setName(hostDTO.getName());
            basicDTO.setIpAddress(hostDTO.getIp());
            basicDTO.setProvider(hostDTO.getProvider());
            basicDTO.setHostUser(hostDTO.getUser());
            basicDTO.setPw(hostDTO.getPwd());
            basicDTO.setCpu(hostDTO.getCpuCount());
            basicDTO.setCpuCores(hostDTO.getCpuCores());
            basicDTO.setCpuSockets(hostDTO.getCpuSockets());
            basicDTO.setCpuProvider(hostDTO.getCpuProvider());
            basicDTO.setStorage(hostDTO.getStorageCapacity());
            basicDTO.setMaintain(hostDTO.getMaintainMode());
            basicDTO.setCvkMaintain(hostDTO.getCvkMaintain());
            basicDTO.setVersion(hostDTO.getVersion());
            basicDTO.setStatus(hostDTO.getStatus());
            basicDTO.setHaEnable(hostDTO.getHaEnable());
            Date addTime = hostDTO.getAddTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = format.format(addTime);
            basicDTO.setAddTime(s);
            try {
                //查询cpuDetail、 hostSystemTime、cpuSuperRatio、memSuperRatio、runTime
                String urlDetail = String.format(CasUriConstants.Host.QUERY_HOST_BY_HOST_ID, hostId);
                HostDetailInfoDTO detailDTO = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, urlDetail, new ParameterizedTypeReference<HostDetailInfoDTO>() {
                        });
                //查询磁盘利用率
                String urlDiskRate = String.format(CasUriConstants.Host.QUERY_HOST_SUMMARY, hostId);
                HostDiskRateDTO rateDTO = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, urlDiskRate, new ParameterizedTypeReference<HostDiskRateDTO>() {
                        });
                basicDTO.setCpuFrequency(detailDTO.getCpuFrequence());
                basicDTO.setCpuDetail(detailDTO.getCpuModel());
                basicDTO.setTotalMemory(UnitUtil.getMemory(detailDTO.getMemory()));
                basicDTO.setFreeMemory(UnitUtil.getMemory(detailDTO.getFreeMemory()));
                basicDTO.setFreeStorage(UnitUtil.getMemory(detailDTO.getFreeStorage()));
                List<KeyValue> keyValue = rateDTO.getKeyValue();
                Map<String, String> map = new HashMap<>();
                keyValue.stream().forEach(str -> {
                    if (StringUtils.isEmpty(str.getValue())) {
                        str.setValue(null);
                    }
                    map.put(str.getKey(), str.getValue());
                });
                basicDTO.setDiskRate(Double.parseDouble(map.get("diskRate")));
                basicDTO.setHostSystemTime(detailDTO.getSystemTime());
                basicDTO.setRunTime(detailDTO.getRunTime());
                basicDTO.setCpuSuperRatio(detailDTO.getCpuSuperRatio());
                basicDTO.setMemSuperRatio(detailDTO.getMemorySuperRatio());
                basicDTO.setILOs(detailDTO.getILOs() == null ? new String[]{} : detailDTO.getILOs().split(";"));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            infos.add(basicDTO);
        });
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.host_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
