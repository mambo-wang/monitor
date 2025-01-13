package com.virtual.cloud.om.workspace.service.report;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VersionDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceResourcePlatformVersionCollector extends DataReportCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        vat.setTags(tags);
        String uri = WsUriConstants.QUERY_RESOURCE_VERSION;
        RpcResult<VersionDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port,
                uri, new ParameterizedTypeReference<RpcResult<VersionDTO>>() {
                }).getBody();
        Utils.checkResult(uri, rpcResult);
        VersionDTO versionDTO = rpcResult.getData();
        String version = versionDTO.getOutVersion();
        vat.setValue(version);
        return Lists.newArrayList(vat);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.workspace_resource_plat_version;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.text;
    }
}
