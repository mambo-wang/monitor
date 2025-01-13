package com.virtual.cloud.om.workspace.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.config.rest.workspace.WsRestConnection;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesktopPoolBasicCollector extends DataReportCollector {
    private final WsRestConnection wsRestConnection;
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password,
                                             String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<DesktopPoolBasicDTO> infos = Lists.newCopyOnWriteArrayList();
        List<DesktopPoolDTO> desktopPoolDTOList;
        {
            String uri = WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_LIST;
            RpcListLoadResult<DesktopPoolDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol,username,password, port,
                    uri, new ParameterizedTypeReference<RpcListLoadResult<DesktopPoolDTO>>() {
                    }).getBody();
            Utils.checkResult(uri, rpcResult);
            desktopPoolDTOList = rpcResult.getData();
        }
        if (CollUtil.isEmpty(desktopPoolDTOList)) {
            return Collections.emptyList();
        }
        CompletableFuture[] completableFutures = desktopPoolDTOList.stream().map(desktopPool ->
                CompletableFuture.supplyAsync(() -> {
                    final Long id = desktopPool.getId();
                    final Integer assignMode = desktopPool.getAssignMode();
                    final Integer computerType = desktopPool.getComputerType();
                    DesktopPoolBasicDTO info = new DesktopPoolBasicDTO();
                    info.setDesktopPoolId(id);
                    info.setAssignMode(assignMode);
                    info.setComputerType(computerType);
                    // 基本信息
                    try{
                        String uri = String.format(WsUriConstants.DesktopPool.QUERY_VMS_STAT, id);
                        RpcResult<DomainStatusStat> domainStatusStatRpcResult = this.wsTokenRestConnection.get(host, protocol,username,password, port,
                                uri, new ParameterizedTypeReference<RpcResult<DomainStatusStat>>() {
                                }).getBody();
                        Utils.checkResult(uri, domainStatusStatRpcResult);
                        DomainStatusStat domainStatusStat = domainStatusStatRpcResult.getData();
                        info.setNoAllocationNum(domainStatusStat.getNoAllocationNum());
                        info.setOnlineNumber(domainStatusStat.getOnlineNumber());
                        info.setAllocationNum(domainStatusStat.getAllocationNum());
                        uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_INFO_BY_ID, id);
                        RpcResult<DesktopPoolDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult<DesktopPoolDTO>>() {
                        }).getBody();
                        Utils.checkResult(uri, rpcResult);
                        DesktopPoolDTO desktopPoolDTO = rpcResult.getData();
                        info.setName(desktopPoolDTO.getName());
                        info.setUserType(desktopPoolDTO.getUserType());
                        info.setTargetType(desktopPoolDTO.getTargetType());
                        info.setClusterName(desktopPoolDTO.getClusterName());
                        info.setDesktopNamePrefix(desktopPoolDTO.getDesktopNamePre());
                        info.setMaxVmNum(desktopPoolDTO.getMaxVmNum());
                        info.setStoragePoolName(desktopPoolDTO.getStoragePoolName());
                        info.setSecondStoragePoolName(desktopPoolDTO.getSecondStoragePoolName());
                        List<String> hostNameList = desktopPoolDTO.getHostNameList();
                        if (!CollectionUtils.isEmpty(hostNameList)) {
                            info.setCvkName(hostNameList.stream().filter(Objects::nonNull).findFirst().get());
                        }
                        // 镜像名称
                        final Long vmTemplateId = desktopPoolDTO.getVmTemplateId();
                        if(Objects.nonNull(vmTemplateId)){
                            uri = String.format(WsUriConstants.Image.QUERY_TEMPLATE_INFO, vmTemplateId);
                            RpcListLoadResult<ImageResponseDTO> imageResponseDTORpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port,
                                    uri,new ParameterizedTypeReference<RpcListLoadResult<ImageResponseDTO>>() {
                                    }).getBody();
                            Utils.checkResult(uri, imageResponseDTORpcResult);
                            List<ImageResponseDTO> imageResponseDTOList = imageResponseDTORpcResult.getData();
                            if (CollUtil.isNotEmpty(imageResponseDTOList)) {
                                imageResponseDTOList.stream().findFirst().ifPresent(image->{
                                    info.setTemplateName(image.getTitle());
                                    info.setTemplateCpu(image.getCpu());
                                    info.setTemplateMemory(image.getMemory());
                                });
                            }
                        }
                        uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOL_BYID, id);
                        RpcResult<RestDesktopPoolDTO> desktopPoolDTORpcResult = this.wsRestConnection.get(host, protocol, port,
                                uri,
                                new RestType<RpcResult<RestDesktopPoolDTO>>() {
                                });
                        Utils.checkResult(uri, desktopPoolDTORpcResult);
                        RestDesktopPoolDTO restDesktopPoolDTO = desktopPoolDTORpcResult.getData();
                        info.setRunningNum(restDesktopPoolDTO.getRunningNum());
                        info.setPausedNum(restDesktopPoolDTO.getPausedNum());
                        info.setAbnormalNum(restDesktopPoolDTO.getAbnormalNum());
                        info.setUnknownNum(restDesktopPoolDTO.getUnknownNum());
                        info.setShutOffNum(restDesktopPoolDTO.getShutOffNum());
                        info.setVmNum(restDesktopPoolDTO.getVmNum());
                        return info;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }).whenCompleteAsync((result, throwable) -> {
                    if(throwable instanceof AppException){
                        log.error("[data collect][resourceId={}][{}][{}] error : {}", resourceId, host, metric(), throwable);
                    }
                    if(Objects.nonNull(result)){
                        infos.add(result);
                    }
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.desktop_pool_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}