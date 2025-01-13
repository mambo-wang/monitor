package com.virtual.cloud.om.cas.service.operate;

import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.CasRsResult;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostDetailInfoDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.StoragePoolDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasStorageOperateCommandExecutor extends OperateCommandApi {
    private final CasRestConnection casRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final String hostId = object.getHostId();
        final String poolName = object.getPoolName();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        {
            ro.setHostId(hostId);
            ro.setPoolName(poolName);
            data.setObject(ro);
        }
        String detailUri = String.format(CasUriConstants.Host.QUERY_HOST_BY_HOST_ID, hostId);
        HostDetailInfoDTO detailDTO = this.casRestConnection.get(platform, host, protocol, port, username, password, detailUri, new ParameterizedTypeReference<HostDetailInfoDTO>() {
        });
        final String hostName = detailDTO.getName();
        this.doOperate(platform, host, protocol, port, username, password, type, hostId, poolName, hostName);
        data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
        String uri = String.format(CasUriConstants.Storage.STORAGE_POOL_IN_HOST_URL, hostId);
        List<StoragePoolDTO> storagePoolDTOList = this.casRestConnection.get(platform, host, protocol, port,
                username, password, uri, new ParameterizedTypeReference<List<StoragePoolDTO>>() {
                });
        storagePoolDTOList.stream().filter(dto -> dto.getName().equals(poolName)).findFirst().ifPresent(dto -> {
            ro.setTotalSize(dto.getTotalSize());
            ro.setPoolStatus(dto.getStatus());
            ro.setAllocation(dto.getAllocation());
            ro.setFreeSize(dto.getFreeSize());
        });
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        return null;
    }

    private void doOperate(String platform, String host, String protocol, Integer port, String username, String password, OperateTypeEnum type, String hostId, String poolName, String hostName) {
        String uri;
        switch (type) {
            case start: {
                uri = new StringBuilder(CasUriConstants.Storage.STORAGE_POOL_START_URL).append("?")
                        .append("id").append("=").append(hostId)
                        .append("&").append("poolName").append("=").append(poolName)
                        .append("&").append("hostName").append("=").append(hostName)
                        .toString();
                break;
            }
            case pause: {
                uri = new StringBuilder(CasUriConstants.Storage.STORAGE_POOL_STOP).append("?")
                        .append("id").append("=").append(hostId)
                        .append("&").append("poolName").append("=").append(poolName)
                        .append("&").append("hostName").append("=").append(hostName)
                        .toString();
                break;
            }
            case refresh: {
                uri = new StringBuilder(CasUriConstants.Storage.STORAGE_POOL_REFRESH_URL).append("?")
                        .append("id").append("=").append(hostId)
                        .append("&").append("poolName").append("=").append(poolName)
                        .append("&").append("hostName").append("=").append(hostName)
                        .toString();
                break;
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
        CasRsResult rpcResult = this.casRestConnection.get(platform, host, protocol, port, username, password, uri, new ParameterizedTypeReference<CasRsResult>() {
        });
        Utils.checkResult(uri, rpcResult);
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.storagePool;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return null;
    }
}
