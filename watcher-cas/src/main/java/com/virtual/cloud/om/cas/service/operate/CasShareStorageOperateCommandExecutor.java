package com.virtual.cloud.om.cas.service.operate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.token.cas.CasTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.CasRsResult;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasShareStorageOperateCommandExecutor extends OperateCommandApi {
    private final CasTokenRestConnection casTokenRestConnection;
    private final CasRestConnection casRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final String clusterId = object.getClusterId();
        final String fileName = object.getShareFileName();
        final String fileType = object.getShareFileType();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setClusterId(clusterId);
        ro.setShareFileName(fileName);
        ro.setShareFileType(fileType);
        data.setObject(ro);
        List<ShareFileDTO> shareFileDTOS = this.casRestConnection.get(platform, host, protocol, port,
                username, password, String.format(CasUriConstants.Storage.STORAGE_FS_BY_CLUSTER_ID, clusterId),
                new ParameterizedTypeReference<List<ShareFileDTO>>() {
                });
        Optional<ShareFileDTO> first = shareFileDTOS.stream().filter(dto -> dto.getName().equals(fileName)).findFirst();
        if (!first.isPresent()) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] cant find ShareFile by clusterId={}", uuid, objectType, type, clusterId);
            return;
        }
        ShareFileDTO dto = first.get();
        ro.setTotalSize(dto.getMaxSize());
        ro.setAllocation(dto.getAllocation());
        ro.setFreeSize(dto.getRemainSize());
        final Long fsId = dto.getId();
        CasShareStorageDto cs = new CasShareStorageDto(clusterId, fileName, fileName, fileType, fsId);
        this.doOperate(platform, host, protocol, port, username, password, type, cs);
        data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        List<HostDTO> hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                username, password, CasUriConstants.Host.HOST_ALL_INFO, new ParameterizedTypeReference<List<HostDTO>>() {
                });
        {
            //通过共享文件id和集群id查询对应的主机id
            String uri = String.format(CasUriConstants.Storage.STORAGE_FS_HOST_BY_CLUSTER_ID_AND_FS_ID, clusterId, fsId);
            List<ShareFileHostDTO> fileHostDTOS = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, uri, new ParameterizedTypeReference<List<ShareFileHostDTO>>() {
                    });
            List<Long> hostIds = fileHostDTOS.stream().map(ShareFileHostDTO::getId).collect(Collectors.toList());
            List<ShareFileHostBasicDTOCopy> collect = hostDTOList.stream().filter(hostDto -> hostIds.contains(hostDto.getId()))
                    .map(hostDto -> {
                        final Long id = hostDto.getId();
                        List<StoragePoolDTO> storagePoolDTOList = this.casRestConnection.get(platform, host, protocol, port,
                                username, password, String.format(CasUriConstants.Storage.STORAGE_POOL_IN_HOST_URL, id),
                                new ParameterizedTypeReference<List<StoragePoolDTO>>() {
                                });
                        return storagePoolDTOList.stream()
                                .filter(storage -> "fs".equals(storage.getType()) && dto.getName().equals(storage.getName()))
                                .map(storage -> {
                                    //共享存储的主机池
                                    ShareFileHostBasicDTOCopy sfhb = new ShareFileHostBasicDTOCopy();
                                    sfhb.setInitiatorName(hostDto.getIscsiNodeName());
                                    sfhb.setHostIp(hostDto.getIp());
                                    sfhb.setHostName(hostDto.getName());
                                    sfhb.setHostStatus(hostDto.getStatus());
                                    sfhb.setPoolStatus(storage.getStatus());
                                    return sfhb;
                                }).collect(Collectors.toList());
                    }).flatMap(Collection::stream).collect(Collectors.toList());
            ro.setShareFileHostList(collect);
        }
        log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        return null;
    }

    private void doOperate(String platform, String host, String protocol, Integer port, String username, String password, OperateTypeEnum type, CasShareStorageDto dto) {
        String uri;
        switch (type) {
            case start: {
                uri = CasUriConstants.Storage.SHARE_STORAGE_START;
                break;
            }
            case pause: {
                uri = CasUriConstants.Storage.SHARE_STORAGE_STOP;
                break;
            }
            case refresh: {
                uri = CasUriConstants.Storage.SHARE_STORAGE_REFRESH;
                break;
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
        Map map = this.casTokenRestConnection.put(host, protocol, username, password, port, uri,
                JSONUtil.toJsonStr(dto), Map.class).getBody();
        CasRsResult casRsResult = BeanUtil.mapToBean(map, CasRsResult.class, true, null);
        Utils.checkResult(uri, casRsResult);
    }

    @Data
    @AllArgsConstructor
    private class CasShareStorageDto {
        private String clusterId;
        private String name;
        private String title;
        private String type;
        private Long id;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.shareStorage;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return null;
    }
}
