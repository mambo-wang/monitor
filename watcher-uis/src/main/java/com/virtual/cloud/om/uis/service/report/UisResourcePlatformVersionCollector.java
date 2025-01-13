package com.virtual.cloud.om.uis.service.report;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UisResourcePlatformVersionCollector extends DataReportCollector {
    private final UisRestConnection uisRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        vat.setTags(tags);
        String uri = UisUriConstants.PLATFORM_VERSION;
        RpcResult rpcResult = this.uisRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult>() {
        }).getBody();
        Utils.checkResult(uri, rpcResult);
        String version = JSONUtil.parseArray(JSONUtil.toJsonStr(rpcResult.getData())).getStr(0);
        vat.setValue(version);
        return Lists.newArrayList(vat);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.uis_resource_plat_version;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.text;
    }
}
