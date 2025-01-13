package com.virtual.cloud.om.uis.service.report;

import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.*;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcPagingLoadResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class UisResourceUserNumberCollector extends DataReportCollector {

    private final UisRestConnection uisRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        Integer value;
        String url = String.format(UisUriConstants.Operator.QUERY_UIS_OPERATOR, 10, 0);
        RpcPagingLoadResult rpcPagingLoadResult = this.uisRestConnection.get(host, protocol, username ,password, port, url, new ParameterizedTypeReference<RpcPagingLoadResult>() {
        }).getBody();
        Utils.checkResult(url, rpcPagingLoadResult);
        value = rpcPagingLoadResult.getTotalLength();
        vat.setValue(value);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.uis_resource_user_number;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }
}
