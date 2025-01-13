package com.virtual.cloud.om.workspace.service.report;

import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.workspace.WsRestConnection;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.TerminalBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VdiDeviceDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerminalBasicCollector extends DataReportCollector {
    private final WsRestConnection wsRestConnection;
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        String uri = WsUriConstants.QUERY_TERMINAL_BASIC;
        ResponseEntity<RpcListLoadResult<VdiDeviceDTO>> responseEntity = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
        });
        if(Objects.isNull(responseEntity)){
            throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR, uri);
        }
        RpcListLoadResult<VdiDeviceDTO> rpcResult = responseEntity.getBody();
        Utils.checkResult(uri, rpcResult);
        List<VdiDeviceDTO> list = rpcResult.getData();
        List<TerminalBasicDTO> value = list.stream().map(device -> {
            TerminalBasicDTO dto = new TerminalBasicDTO();
            dto.setDeviceId(String.valueOf(device.getId()));
            dto.setDeviceUuid(device.getDeviceId());
            dto.setDisplayName(device.getDisplayName());
            dto.setIpAddress(device.getIpAddr());
            dto.setMacAddress(device.getMacAddr());
            dto.setDeviceName(device.getDeviceName());
            dto.setIsDenyList(device.getDeny());
            dto.setDeviceRegisterTime(device.getDeviceRegistTime());
            dto.setOsType(device.getOsType());
            dto.setCpuArch(device.getCpuArch());
            dto.setVendor(device.getVendor());
            dto.setModel(device.getModel());
            dto.setClientVersion(device.getClientVersion());
            dto.setSpaceAgentVersion(device.getSpaceagentVersion());
            dto.setOsVersion(device.getOsVersion());
            dto.setSn(device.getSn());
            dto.setDeviceType(device.getDeviceType());
            dto.setAuthType(device.getAuthType());
            dto.setStatus(device.getStatus());
            dto.setDeviceGroupId(device.getDeviceGroupId());
            dto.setDeviceGroupName(device.getDeviceGroupName());
            return dto;
        }).collect(Collectors.toList());
        vat.setValue(value);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
        /**
         * TODO 下面代码是rest接口，为了兼容老版本暂时使用token接口，请勿删除
         */
//        String uri = WsUriConstants.QUERY_TERMINAL_BASIC_REST;
//        RpcResult<List<TerminalBasicDTO>> rpcResult = this.wsRestConnection.get(host, protocol, port,
//                uri,
//                new RestType<RpcResult<List<TerminalBasicDTO>>>() {
//                });
//        Utils.checkResult(uri, rpcResult);
//        return rpcResult.getData();
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.terminal_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
