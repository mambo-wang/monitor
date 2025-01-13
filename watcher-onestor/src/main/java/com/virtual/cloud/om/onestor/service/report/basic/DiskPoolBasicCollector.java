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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.*;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorDiskPoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorDiskPoolBasicReportDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OneStor硬盘池基本信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiskPoolBasicCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<StorDiskPoolBasicReportDTO> infos = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        //OneStor管理节点调用接口后获取集群内的所有相关信息
        try {
            String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
            //获取所有的diskPool
            String nodePoolUrl = String.format(OnestoreUriConstants.NodePool.BASIC_INFO_NODE_POOL, clusterId);
            OneStorRestResult nodePoolRestResult = onestorRestConnection.get(host, protocol, username, password, port, nodePoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap nodePoolData = (LinkedHashMap)nodePoolRestResult.getData();
            List<StorNodePoolBasicDTO> nodePoolDTOList = mapper.convertValue(nodePoolData.get("nodepool_list"), new TypeReference<List<StorNodePoolBasicDTO>>(){});
            nodePoolDTOList.forEach(nodePool -> {
                String diskPoolUrl = String.format(OnestoreUriConstants.DiskPool.BASIC_INFO_DISK_POOL, clusterId, nodePool.getNodepool_name());
                OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, diskPoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
                LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
                List<StorDiskPoolBasicDTO> dtoList = mapper.convertValue(data.get("diskpool_list"), new TypeReference<List<StorDiskPoolBasicDTO>>(){});
                dtoList.forEach(dto -> {
                    StorDiskPoolBasicReportDTO reportDto = new StorDiskPoolBasicReportDTO();
                    BeanUtil.copyProperties(dto, reportDto, new String[]{"encrypt_config", "is_tiered_storage", "safe_domain_flag"});
                    reportDto.setFs_id(clusterId);
                    reportDto.setNodepool_name(nodePool.getNodepool_name());
                    reportDto.setNodepool_list(Lists.newArrayList(nodePool.getNodepool_name()));
                    reportDto.setEncrypt_config(dto.getEncrypt_config());
                    reportDto.setIs_tiered_storage(dto.is_tiered_storage());
                    reportDto.setSafe_domain_flag(dto.getSafe_domain_flag());
                    reportDto.setStatus_reason(dto.getStatus().getReason());
                    reportDto.setStatus_reason_eng(dto.getStatus().getReason_eng());
                    reportDto.setStatus(dto.getStatus().getStatus());
                    infos.add(reportDto);
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
        return DataReportTypeByMetricEnum.stor_diskpool_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
