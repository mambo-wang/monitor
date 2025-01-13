package com.virtual.cloud.om.cas.service.report;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.PlatformVersionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasResourcePlatformVersionCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        vat.setTags(tags);
        String uri = CasUriConstants.PLATFORM_VERSION;
        PlatformVersionDTO platformVersionDTO = this.casRestConnection.get(platform, host, protocol, port,
                username, password, uri, new ParameterizedTypeReference<PlatformVersionDTO>() {
                });
        String version = platformVersionDTO.getCasVersion();
        vat.setValue(version);
        return Lists.newArrayList(vat);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cas_resource_plat_version;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.text;
    }
}
