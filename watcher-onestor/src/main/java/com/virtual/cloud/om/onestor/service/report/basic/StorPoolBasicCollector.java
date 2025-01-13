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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorPoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorPoolBasicReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OneStor存储池基本信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorPoolBasicCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<StorPoolBasicReportDTO> infos = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        //OneStor管理节点调用接口后获取集群内的所有相关信息
        try {
            String clusterId = onestorRestConnection.getClusterId(host, protocol, username, password, port);
            String diskPoolUrl = String.format(OnestoreUriConstants.Pool.BASIC_INFO_POOL, clusterId);
            OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, diskPoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
            List<StorPoolBasicDTO> dtoList = mapper.convertValue(data.get("pool_list"), new TypeReference<List<StorPoolBasicDTO>>(){});
            dtoList.forEach(dto -> {
                StorPoolBasicReportDTO reportDto = new StorPoolBasicReportDTO();
                BeanUtil.copyProperties(dto, reportDto, new String[]{"status","cache_tier_enable"});
                reportDto.setFs_id(clusterId);
                reportDto.setPool_id(dto.getId());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                reportDto.setUpdate_time(simpleDateFormat.format(new Date()));
                reportDto.setStatusValue(dto.getStatus().getStatus());
                reportDto.setCache_tier_enable(dto.getCache_tier_enable());
                infos.add(reportDto);
            });

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
        return DataReportTypeByMetricEnum.stor_storage_pool_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
