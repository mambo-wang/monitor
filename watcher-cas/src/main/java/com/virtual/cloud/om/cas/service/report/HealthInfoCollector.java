package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HealthInfoBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HealthInfoDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthInfoCollector extends DataReportCollector {

    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<HealthInfoBasicDTO> infos = Lists.newArrayList();
        //查询主机健康度的信息
        String url = CasUriConstants.Host.QUERY_HOST_DASHBOARD_HEALTH;
        List<HealthInfoDTO> healthInfoDTOList = null;
        try {
            healthInfoDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, url, new ParameterizedTypeReference<List<HealthInfoDTO>>() {
                    });
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        if (CollUtil.isEmpty(healthInfoDTOList)) {
            return Collections.emptyList();
        }
        healthInfoDTOList.forEach(healthInfoDTO -> {
            HealthInfoBasicDTO healthInfoBasicDTO = new HealthInfoBasicDTO();
            healthInfoBasicDTO.setHostId(healthInfoDTO.getHostId());
            healthInfoBasicDTO.setCvkHealth(healthInfoDTO.getCvkHealth());
            if (!ObjectUtil.isEmpty(healthInfoBasicDTO.getCvkHealth()) && !ObjectUtil.isEmpty(healthInfoBasicDTO.getHostId())) {
                infos.add(healthInfoBasicDTO);
            }
        });
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.health_info;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
