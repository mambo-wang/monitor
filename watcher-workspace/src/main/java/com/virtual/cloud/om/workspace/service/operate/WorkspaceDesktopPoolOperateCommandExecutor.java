package com.virtual.cloud.om.workspace.service.operate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.config.rest.workspace.WsRestConnection;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcPagingLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DesktopPoolDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DomainDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DomainStatusStat;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VdiDeviceDTO;
import com.virtual.cloud.om.sdk.dto.operate.CasRsTaskMsg;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.dto.operate.RefreshStatusResultDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceDesktopPoolOperateCommandExecutor extends OperateCommandApi {
    private final WsTokenRestConnection wsTokenRestConnection;
    private final WsRestConnection wsRestConnection;
    private final CasRestConnection casRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final Long desktopPoolId = object.getDesktopPoolId();
        String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOL_BY_ID, desktopPoolId);
        RpcResult<DesktopPoolDTO> rpcResult = this.wsRestConnection.get(host, protocol, port, uri, new RestType<RpcResult<DesktopPoolDTO>>() {
        });
        Utils.checkResult(uri, rpcResult);
        DesktopPoolDTO desktopPool = rpcResult.getData();
        switch (desktopPool.getComputerType()) {
            case 0: {
                this.desktopPoolDomain(desktopPoolId, platform, host, port, protocol, username, password, uuid, objectType, type, data);
                log.info("[operate command][uuid={}][objectType={}][operateType={}] desktop pool domain operate end", uuid, objectType, type);
                break;
            }
            default: {
                this.desktopPoolTerminal(desktopPoolId, platform, host, port, protocol, username, password, uuid, objectType, type, data);
                log.info("[operate command][uuid={}][objectType={}][operateType={}] desktop pool terminal operate end", uuid, objectType, type);
            }
        }
    }

    private void desktopPoolDomain(Long desktopPoolId, String platform, String host, Integer port, String protocol, String username, String password,
                                   String uuid, ObjectTypeEnum objectType, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_VMS, desktopPoolId);
        RpcListLoadResult<DomainDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<DomainDTO>>() {
        }).getBody();
        Utils.checkResult(uri, rpcListLoadResult);
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setDesktopPoolType(0);
        ro.setDesktopPoolId(desktopPoolId);
        data.setObject(ro);
        List<DomainDTO> domains = rpcListLoadResult.getData();
        List<CasRsTaskMsg> taskMsgList = Lists.newCopyOnWriteArrayList();
        {
            CompletableFuture[] completableFutures = domains.stream().map(domain ->
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            CasRsTaskMsg casRsTaskMsg = this.addTask(platform, host, protocol, port, username, password, domain.getUuid(), type);
                            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate task complete !", uuid, objectType, type);
                            return casRsTaskMsg.getMsgId();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).whenCompleteAsync((result, t) -> {
                        if (Objects.nonNull(result)) {
                            CasRsTaskMsg casRsTaskMsg = this.queryCasTaskMsg(platform, host, protocol, port, username, password, result);
                            if (Objects.nonNull(casRsTaskMsg)) {
                                taskMsgList.add(casRsTaskMsg);
                                if (StateResult.FAILURE == casRsTaskMsg.getResult()) {
                                    String errorMsg = StrUtil.isNotBlank(casRsTaskMsg.getFailMsg()) ? casRsTaskMsg.getFailMsg() : casRsTaskMsg.getDetail();
                                    log.info("[operate command][uuid={}][objectType={}][operateType={}][domainId={}] operate fail : {}", uuid, objectType, type, domain.getId(), errorMsg);
                                    data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
                                    data.setFailureMessage(errorMsg);
                                } else {
                                    log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
                                    data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
                                }
                            }
                        }
                    })
            ).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(completableFutures).join();
        }
        long failTaskSize = taskMsgList.stream().filter(msg -> msg.getResult().equals(StateResult.FAILURE)).count();
        if (taskMsgList.size() == 0 || failTaskSize == Integer.valueOf(domains.size()).longValue()) {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate all fail", uuid, objectType, type);
        } else if (taskMsgList.size() < domains.size() || failTaskSize > 0L) {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.part_success.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate part success", uuid, objectType, type);
        } else {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
        }
        {
            List<OperateResultDTO.DataDTO.ResultObject.Target> targets = this.queryDesktopPoolDomainStatus(domains, platform, host,
                    port, protocol, username, password);
            ro.setTargetList(targets);
        }
    }

    private void desktopPoolTerminal(Long desktopPoolId, String platform, String host, Integer port, String protocol, String username, String password,
                                     String uuid, ObjectTypeEnum objectType, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_TERMINALS, desktopPoolId);
        RpcListLoadResult<VdiDeviceDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
        }).getBody();
        Utils.checkResult(uri, rpcListLoadResult);
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setDesktopPoolType(1);
        ro.setDesktopPoolId(desktopPoolId);
        data.setObject(ro);
        List<VdiDeviceDTO> devices = rpcListLoadResult.getData();
        List<Boolean> resultList = Lists.newCopyOnWriteArrayList();
        {
            CompletableFuture[] completableFutures = devices.stream().map(device ->
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.doTerminalOperate(host, port, protocol, username, password, type, device.getId());
                            resultList.add(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            resultList.add(false);
                        }
                    })
            ).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(completableFutures).join();
        }
        long failSize = resultList.stream().filter(r -> !r).count();
        if (CollUtil.isEmpty(resultList) || failSize == Integer.valueOf(devices.size()).longValue()) {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate fail", uuid, objectType, type);
        } else if (resultList.size() < devices.size() || failSize > 0L) {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.part_success.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate part success", uuid, objectType, type);
        } else {
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
        }
        {
            List<OperateResultDTO.DataDTO.ResultObject.Target> targets = this.queryDesktopPoolTerminalStatus(devices, platform, host,
                    port, protocol, username, password);
            ro.setTargetList(targets);
        }
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        final ObjectTypeEnum objectType = this.type();
        final Long desktopPoolId = object.getDesktopPoolId();
        RefreshStatusResultDTO.DesktopPool desktopPool = new RefreshStatusResultDTO.DesktopPool();
        desktopPool.setDesktopPoolId(desktopPoolId);
        desktopPool.setUuid(uuid);
        desktopPool.setResourceId(resourceId);
        desktopPool.setTargetList(Lists.newArrayList());
        try {
            String uri = WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_LIST;
            RpcListLoadResult<DesktopPoolDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol,username,password, port,
                    uri,new ParameterizedTypeReference<RpcListLoadResult<DesktopPoolDTO>>() {
                    }).getBody();
            Utils.checkResult(uri, rpcResult);
            List<DesktopPoolDTO> desktopPoolDTOList = rpcResult.getData();
            Optional<DesktopPoolDTO> first = desktopPoolDTOList.stream().filter(d -> d.getId().equals(desktopPoolId)).findFirst();
            if(!first.isPresent()){
                log.error("[refresh status][uuid={}][objectType={}] cant find desktop pool id={}", uuid, objectType, desktopPoolId);
            }
            first.ifPresent(d -> {
                String vmStatUri = String.format(WsUriConstants.DesktopPool.QUERY_VMS_STAT, desktopPoolId);
                RpcResult<DomainStatusStat> domainStatusStatRpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port,
                        vmStatUri, new ParameterizedTypeReference<RpcResult<DomainStatusStat>>() {
                        }).getBody();
                Utils.checkResult(vmStatUri, domainStatusStatRpcResult);
                DomainStatusStat domainStatusStat = domainStatusStatRpcResult.getData();
                desktopPool.setOnlineNumber(domainStatusStat.getOnlineNumber());
                desktopPool.setNoAllocationNum(domainStatusStat.getNoAllocationNum());
                desktopPool.setAllocationNum(domainStatusStat.getAllocationNum());
                List<OperateResultDTO.DataDTO.ResultObject.Target> targetList;
                switch (d.getComputerType()) {
                    case 0: {
                        desktopPool.setType(0);
                        targetList = this.refreshDomain(desktopPoolId, platform, host, port, protocol, username, password);
                        break;
                    }
                    default: {
                        desktopPool.setType(1);
                        targetList = this.refreshTerminal(desktopPoolId, platform, host, port, protocol, username, password);
                    }
                }
                log.info("[refresh status][uuid={}][objectType={}] select desktopPool status success id={}", uuid, objectType, desktopPoolId);
                desktopPool.setTargetList(targetList);
            });
        } catch (Exception e) {
            log.info("[refresh status][uuid={}][objectType={}] select desktopPool status error id={} : {}", uuid, objectType, desktopPoolId, e);
            desktopPool.setFailMsg(e.getMessage());
        }
        return desktopPool;
    }

    private List<OperateResultDTO.DataDTO.ResultObject.Target> refreshDomain(Long desktopPoolId, String platform, String host, Integer port, String protocol,
                                                                             String username, String password) {
        String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_VMS, desktopPoolId);
        RpcListLoadResult<DomainDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<DomainDTO>>() {
        }).getBody();
        Utils.checkResult(uri, rpcListLoadResult);
        List<DomainDTO> domains = rpcListLoadResult.getData();
        return this.queryDesktopPoolDomainStatus(domains, platform, host,
                port, protocol, username, password);
    }

    private List<OperateResultDTO.DataDTO.ResultObject.Target> queryDesktopPoolDomainStatus(List<DomainDTO> domains, String platform, String host,
                                                                                            Integer port, String protocol, String username, String password) {
        List<OperateResultDTO.DataDTO.ResultObject.Target> targets = Lists.newCopyOnWriteArrayList();
        CompletableFuture[] completableFutures = domains.stream().map(domain ->
                CompletableFuture.runAsync(() -> {
                    final Long domainId = domain.getId();
                    OperateResultDTO.DataDTO.ResultObject.Target target = new OperateResultDTO.DataDTO.ResultObject.Target();
                    target.setTargetId(String.valueOf(domainId));
                    try {
                        Integer status = this.getStatus(this.casRestConnection.get(platform, host, protocol, port, username, password,
                                String.format(CasUriConstants.Domain.QUERY_DOMAIN_STATUS, domainId), new ParameterizedTypeReference<String>() {
                                }));
                        target.setTargetStatus(status);
                    } catch (Exception e) {
                        target.setTargetStatus(this.getStatus("shutOff"));
                    }
                    targets.add(target);
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        return targets;
    }

    private List<OperateResultDTO.DataDTO.ResultObject.Target> refreshTerminal(Long desktopPoolId, String platform, String host, Integer port, String protocol,
                                                                               String username, String password) {
        String uri = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_TERMINALS, desktopPoolId);
        RpcListLoadResult<VdiDeviceDTO> rpcListLoadResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
        }).getBody();
        Utils.checkResult(uri, rpcListLoadResult);
        List<VdiDeviceDTO> devices = rpcListLoadResult.getData();
        return this.queryDesktopPoolTerminalStatus(devices, platform, host,
                port, protocol, username, password);
    }

    private List<OperateResultDTO.DataDTO.ResultObject.Target> queryDesktopPoolTerminalStatus(List<VdiDeviceDTO> devices, String platform, String host,
                                                                                              Integer port, String protocol, String username, String password) {
        List<OperateResultDTO.DataDTO.ResultObject.Target> targets = Lists.newCopyOnWriteArrayList();
        CompletableFuture[] completableFutures = devices.stream().map(device ->
                CompletableFuture.runAsync(() -> {
                    final Long deviceId = device.getId();
                    OperateResultDTO.DataDTO.ResultObject.Target target = new OperateResultDTO.DataDTO.ResultObject.Target();
                    target.setTargetId(String.valueOf(deviceId));
                    RpcListLoadResult<VdiDeviceDTO> rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, WsUriConstants.QUERY_TERMINAL_BASIC, new ParameterizedTypeReference<RpcListLoadResult<VdiDeviceDTO>>() {
                    }).getBody();
                    Utils.checkResult(WsUriConstants.QUERY_TERMINAL_BASIC, rpcResult);
                    List<VdiDeviceDTO> list = rpcResult.getData();
                    list.stream().filter(dev -> dev.getId().equals(deviceId)).findFirst().ifPresent(dev -> {
                        target.setTargetStatus(dev.getStatus());
                    });
                    targets.add(target);
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        return targets;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.desktoppool;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return WebsocketPushTypeEnum.reportDesktopPoolStatus;
    }
}
