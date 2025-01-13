package com.virtual.cloud.om.cas.service.operate;

import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.HostInfo;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.dto.operate.CasRsTaskMsg;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.dto.operate.RefreshStatusResultDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("all")
public class CasHostOperateCommandExecutor extends OperateCommandApi {
    private final CasRestConnection casRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final String hostId = object.getHostId();
        final String clusterId = object.getClusterId();
        final Integer maintainMode = object.getMaintainMode();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setHostId(hostId);
        data.setObject(ro);
        HostInfo hostInfo = this.casRestConnection.get(platform, host, protocol, port, username, password,
                String.format(CasUriConstants.Host.HOST_BASIC_INFO, hostId), new ParameterizedTypeReference<HostInfo>() {
                });
        if (Objects.isNull(hostInfo)) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] cant find host by hostId={}", uuid, objectType, type, hostId);
            return;
        }
        CasRsTaskMsg casRsTaskMsg = this.addHostTask(platform, host, port, protocol, username, password, type, hostId, clusterId, maintainMode, hostInfo.getName());
        log.info("[operate command][uuid={}][objectType={}][operateType={}] operate task complete !", uuid, objectType, type);
        casRsTaskMsg = this.queryCasTaskMsg(platform, host, protocol, port, username, password, casRsTaskMsg.getMsgId());
        if (Objects.isNull(casRsTaskMsg)) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] queryCasTaskMsg fail !", uuid, objectType, type);
            return;
        }
        if (StateResult.FAILURE == casRsTaskMsg.getResult()) {
            String errorMsg = StrUtil.isNotBlank(casRsTaskMsg.getFailMsg()) ? casRsTaskMsg.getFailMsg() : casRsTaskMsg.getDetail();
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate fail : {}", uuid, objectType, type, errorMsg);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            data.setFailureMessage(errorMsg);
        } else {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        }
        // 关机和重启会导致主机状态查询接口异常
        if (type != OperateTypeEnum.stop && type != OperateTypeEnum.restart) {
            hostInfo = this.casRestConnection.get(platform, host, protocol, port, username, password,
                    String.format(CasUriConstants.Host.HOST_BASIC_INFO, hostId), new ParameterizedTypeReference<HostInfo>() {
                    });
        }
        if (Objects.nonNull(hostInfo)) {
            ro.setCvkMaintain(hostInfo.getCvkMaintain());
            ro.setMaintain(hostInfo.getMaintainMode());
            ro.setHostStatus(hostInfo.getStatus());
        }
        // 运维中心确认，关机操作返回status=0
        if (type == OperateTypeEnum.stop) {
            ro.setHostStatus(0);
        }
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        final ObjectTypeEnum objectType = this.type();
        final String hostId = object.getHostId();
        RefreshStatusResultDTO.Host hostDto = new RefreshStatusResultDTO.Host();
        hostDto.setHostId(hostId);
        hostDto.setUuid(uuid);
        hostDto.setResourceId(resourceId);
        try {
            HostInfo hostInfo = this.casRestConnection.get(platform, host, protocol, port, username, password,
                    String.format(CasUriConstants.Host.HOST_BASIC_INFO, hostId), new ParameterizedTypeReference<HostInfo>() {
                    });
            if (Objects.nonNull(hostInfo)) {
                hostDto.setCvkMaintain(hostInfo.getCvkMaintain());
                hostDto.setMaintain(hostInfo.getMaintainMode());
                hostDto.setHostStatus(hostInfo.getStatus());
            }
        } catch (Exception e) {
            hostDto.setFailMsg(e.getMessage());
            log.error("[refresh status][uuid={}][objectType={}] select host status error id={} : {}", uuid, objectType, hostId, e);
        }
        return hostDto;
    }

    private CasRsTaskMsg addHostTask(String platform, String host, Integer port, String protocol, String username, String
            password, OperateTypeEnum type, String hostId, String clusterId, Integer maintainMode, String name) {
        QueryDTO dto = new QueryDTO(hostId, clusterId, name);
        switch (type) {
            case wake: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, CasUriConstants.HOST_WAKE, dto, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case stop: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, CasUriConstants.HOST_SHUTDOWN, dto, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case restart: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, CasUriConstants.HOST_RESTART, dto, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case intoMaintain: {
                dto.setMaintainMode(maintainMode);
                return this.casRestConnection.put(platform, host, protocol, port, username, password, CasUriConstants.HOST_INTO, dto, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case exitMaintain: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, CasUriConstants.HOST_EXIT, dto, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
    }

    @Data
    @RequiredArgsConstructor
    private class QueryDTO {
        private final String id;
        private final String clusterId;
        private final String name;
        private Integer maintainMode;
        ;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.host;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return WebsocketPushTypeEnum.reportServerStatus;
    }
}
