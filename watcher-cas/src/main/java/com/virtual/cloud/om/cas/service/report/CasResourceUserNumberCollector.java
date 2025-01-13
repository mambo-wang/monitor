package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.CasResourceUserNumberDTO;
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
public class CasResourceUserNumberCollector extends DataReportCollector {

    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        Integer value;
        String url;
        url = CasUriConstants.Operator.QUERY_OPERATOR;
        List<CasResourceUserNumberDTO> casResourceUserNumberDTOList = null;
        try {
            casResourceUserNumberDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, url, new ParameterizedTypeReference<List<CasResourceUserNumberDTO>>() {
                    });
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        if (CollUtil.isEmpty(casResourceUserNumberDTOList)) {
            return Collections.emptyList();
        }
        value = casResourceUserNumberDTOList.size();
        vat.setValue(value);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cas_resource_user_number;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }
}
