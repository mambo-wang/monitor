package com.virtual.cloud.om.workspace.service.report;

import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.*;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
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
public class WorkspaceResourceUserNumberCollector extends DataReportCollector {

    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        Integer value = 0;
        //查询本地用户、域用户、LDAP用户作为普通用户，和管理员用户
        List<String> uriList = of(
                String.format(WsUriConstants.ResourceUser.QUERY_USER, WsUserTypeEnum.local_user.getType(), 0, 10),
                String.format(WsUriConstants.ResourceUser.QUERY_USER, WsUserTypeEnum.domain_user.getType(), 0, 10),
                String.format(WsUriConstants.ResourceUser.QUERY_USER, WsUserTypeEnum.ldap_user.getType(), 0, 10),
                String.format(WsUriConstants.ResourceUser.OPERATOR, 0, 10)
        );
        for (String uri:uriList){
            RpcPagingLoadResult rpcPagingLoadResult = this.wsTokenRestConnection.get(host, protocol, username ,password, port, uri, new ParameterizedTypeReference<RpcPagingLoadResult>() {
            }).getBody();
            Utils.checkResult(uri, rpcPagingLoadResult);
            value += rpcPagingLoadResult.getTotalLength();
        }
        vat.setValue(value);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.workspace_resource_user_number;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }
}
