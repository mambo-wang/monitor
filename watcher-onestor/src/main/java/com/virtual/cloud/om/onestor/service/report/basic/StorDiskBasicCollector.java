package com.virtual.cloud.om.onestor.service.report.basic;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/12/2 17:13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorDiskBasicCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<OnestorDiskBasticUploadDTO> infos = Lists.newArrayList();
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
                String DiskUrl = String.format(OnestoreUriConstants.DiskPool.BASIC_INFO_DISK, clusterId,role.getName());
                OneStorRestResult DiskRestResult = onestorRestConnection.get(host, protocol, username, password, port, DiskUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
                LinkedHashMap nodePoolData = (LinkedHashMap)DiskRestResult.getData();
                List<OnestorDiskBasticDTO> nodePoolDTOList = mapper.convertValue(nodePoolData.get("disk_info_list"), new TypeReference<List<OnestorDiskBasticDTO>>(){});
                nodePoolDTOList.stream().forEach(s->{
                    OnestorDiskBasticUploadDTO o =new OnestorDiskBasticUploadDTO();
                    o.setEncyrptConfig(Objects.nonNull(s.getEncrypt_config())?s.getEncrypt_config():"off");
                    o.setDiskpoolName(s.getDiskpool_name());
                    o.setHostName(role.getName());
                    o.setLogicalDiskName(s.getLogical_disk_name());
                    o.setLogicalDiskSize(s.getLogical_disk_size());
                    o.setLogicalDiskType(s.getLogical_disk_type());
                    o.setPhysicalDiskSize(s.getPhysical_disk_size());
                    o.setStatus(s.getStatus());
                    o.setUsedBytes(s.getUsed_bytes());
                    o.setUsedPct(s.getUsed_pct());
                    infos.add(o);
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        log.info("result: {}",vat);
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_disk_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
