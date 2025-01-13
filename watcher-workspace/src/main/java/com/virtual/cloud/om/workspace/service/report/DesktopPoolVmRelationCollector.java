package com.virtual.cloud.om.workspace.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DesktopPoolDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DesktopPoolVmRelationDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DomainDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VdiDeviceDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesktopPoolVmRelationCollector extends DataReportCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<DesktopPoolVmRelationDTO> infos = Lists.newCopyOnWriteArrayList();
        List<DesktopPoolDTO> desktopPoolDTOList;
        {
            String uri = WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_LIST;
            RpcListLoadResult<DesktopPoolDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port,
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
                    final Integer computerType = desktopPool.getComputerType();
                    try {
                        switch (computerType) {
                            case 0: {
                                String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_VMS, id);
                                RpcListLoadResult<DomainDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<DomainDTO>>() {
                                }).getBody();
                                List<DomainDTO> domains = rpcListLoadResult.getData();
                                return domains.stream().map(domain -> {
                                    DesktopPoolVmRelationDTO info = new DesktopPoolVmRelationDTO();
                                    info.setComputerType(computerType);
                                    info.setVmUuid(domain.getUuid());
                                    info.setDesktopPoolId(id);
                                    return info;
                                }).collect(Collectors.toList());
                            }
                            default: {
                                String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_TERMINALS, id);
                                RpcListLoadResult<VdiDeviceDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
                                }).getBody();
                                Utils.checkResult(uri, rpcListLoadResult);
                                List<VdiDeviceDTO> devices = rpcListLoadResult.getData();
                                return devices.stream().map(device -> {
                                    DesktopPoolVmRelationDTO info = new DesktopPoolVmRelationDTO();
                                    info.setComputerType(computerType);
                                    info.setVmUuid(String.valueOf(device.getId()));
                                    info.setDesktopPoolId(id);
                                    return info;
                                }).collect(Collectors.toList());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).whenCompleteAsync((result, throwable) -> {
                    if (throwable instanceof AppException) {
                        log.error("[data collect][resourceId={}][{}][{}] error : {}", resourceId, host, metric(), throwable);
                    }
                    if (Objects.nonNull(result)) {
                        infos.addAll(result);
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
        return DataReportTypeByMetricEnum.desktop_pool_vm_relation;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
