package com.virtual.cloud.om.workspace.service.operate;

import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VdiDeviceDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.dto.operate.RefreshStatusResultDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceTerminalOperateCommandExecutor extends OperateCommandApi {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final Long terminalId = object.getDeviceId();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setDeviceId(terminalId);
        data.setObject(ro);
        this.doTerminalOperate(host, port, protocol, username, password, type, terminalId);
        data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
        String uri = WsUriConstants.QUERY_TERMINAL_BASIC;
        RpcListLoadResult<VdiDeviceDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
        }).getBody();
        Utils.checkResult(uri, rpcResult);
        List<VdiDeviceDTO> list = rpcResult.getData();
        list.stream().filter(device -> device.getId().equals(terminalId)).findFirst().ifPresent(device -> {
            ro.setDeviceStatus(device.getStatus());
        });
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        final ObjectTypeEnum objectType = this.type();
        final Long deviceId = object.getDeviceId();
        RefreshStatusResultDTO.Device device = new RefreshStatusResultDTO.Device();
        device.setDeviceId(deviceId);
        device.setUuid(uuid);
        device.setResourceId(resourceId);
        try {
            String uri = WsUriConstants.QUERY_TERMINAL_BASIC;
            RpcListLoadResult<VdiDeviceDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
            }).getBody();
            Utils.checkResult(uri, rpcResult);
            List<VdiDeviceDTO> list = rpcResult.getData();
            list.stream().filter(dev -> dev.getId().equals(deviceId)).findFirst().ifPresent(dev -> {
                device.setDeviceStatus(dev.getStatus());
            });
            log.info("[refresh status][uuid={}][objectType={}] select device status success id={}", uuid, objectType, deviceId);
        } catch (Exception e) {
            log.error("[refresh status][uuid={}][objectType={}] select device status error id={} : {}", uuid, objectType, deviceId, e);
            device.setFailMsg(e.getMessage());
        }
        return device;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.terminal;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return WebsocketPushTypeEnum.reportDeviceStatus;
    }
}
