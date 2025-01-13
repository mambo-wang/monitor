package com.virtual.cloud.om.workspace.service.batchlog;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.dto.logBatch.VmLogDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceVmLogCollector implements LogBatchCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();
        String logDirPath = new StringBuilder(logFileDirPath).append(logFileDirPath.endsWith(File.separator) ? Strings.EMPTY : File.separator).append(logBatchType()).toString();
        List<DownloadResultEnum> returnList = Lists.newCopyOnWriteArrayList();
        CompletableFuture[] completableFutures = Arrays.stream(targets).map(target ->
                CompletableFuture.runAsync(() -> {
                    final String id = target.getId();
                    final String title = target.getTitle();
                    final String collectUri = String.format(WsUriConstants.VM_LOG_COLLECT, title, id);
                    // 收集日志
                    RpcResult<String> rpcCollect = this.wsTokenRestConnection.post(host, protocol, username, password, port, collectUri, "", new ParameterizedTypeReference<RpcResult<String>>() {
                    }).getBody();
                    try {
                        Utils.checkResult(collectUri, rpcCollect);
                    } catch (AppException e) {
                        log.error("[log batch][ticket={}][{}] collect log files apply error :{}", ticket, this.logBatchType(), e);
                        return;
                    }
                    log.info("[log batch][ticket={}][{}] collect log files apply success!", ticket, this.logBatchType());
                    final String vmLogsUri = WsUriConstants.VM_LOGS;
                    // 查询收集结果
                    VmLogDTO vmLog = this.vmLogsFromWs(host, protocol, username, password, port, vmLogsUri, title, 1, ticket);
                    if (Objects.nonNull(vmLog) && vmLog.getStatus().equals(1)) {
                        returnList.add(DownloadResultEnum.success);
                        try {
                            final String fileName = vmLog.getFileName();
                            // 下载收集文件
                            log.info("[log batch][ticket={}][{}] download log file start!", ticket, this.logBatchType());
                            this.wsTokenRestConnection.downloadBigFile(host, protocol, username, password, port,
                                    String.format(WsUriConstants.VM_LOG_DOWNLOAD, vmLog.getId()),
                                    logDirPath, fileName.substring(0, fileName.lastIndexOf(".")), ".zip");
                            log.info("[log batch][ticket={}][{}] download log file success!", ticket, this.logBatchType());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        returnList.add(DownloadResultEnum.fail);
                        log.info("[log batch][ticket={}][{}] collect log files fail , msg:{}", ticket, this.logBatchType(), vmLog.getDescription());
                    }
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        log.info("[log batch][ticket={}][{}] collect log files complete ! time={}ms", ticket, this.logBatchType(), (System.currentTimeMillis() - startTime));
        Optional<DownloadResultEnum> success = returnList.stream().filter(e -> e == DownloadResultEnum.success).findFirst();
        Optional<DownloadResultEnum> fail = returnList.stream().filter(e -> e == DownloadResultEnum.fail).findFirst();
        if (success.isPresent() && fail.isPresent()) {
            return DownloadResultEnum.part_success;
        } else if (success.isPresent() && !fail.isPresent()) {
            return DownloadResultEnum.success;
        } else {
            return DownloadResultEnum.fail;
        }
    }

    /**
     * 获取日志收集记录列表
     *
     * @param host
     * @param protocol
     * @param username
     * @param password
     * @param port
     * @param vmLogsUri
     * @param title
     * @param times
     * @param ticket
     * @return 如果此方法内部发生异常，算作失败，返回null
     */
    private VmLogDTO vmLogsFromWs(String host, String protocol, String username, String password, Integer port, String vmLogsUri, String title, Integer times, String ticket) {
        try {
            final int maxRecursiveNum = 5;
            String vmLogsRpcStr = this.wsTokenRestConnection.get(host, protocol, username, password, port, vmLogsUri, new ParameterizedTypeReference<String>() {
            }).getBody();
            RpcResult vmLogsRpc = JSONUtil.toBean(vmLogsRpcStr, RpcResult.class);
            Utils.checkResult(vmLogsUri, vmLogsRpc);
            try {
                log.info("[log batch][ticket={}][{}] recursive query collect log files result, thread sleep", ticket, logBatchType());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            List<VmLogDTO> vmLogs = JSONUtil.toList(JSONUtil.parseObj(vmLogsRpc.getData()).getStr("data"), VmLogDTO.class);
            if (CollUtil.isEmpty(vmLogs)) {
                if (times > maxRecursiveNum) {
                    log.info("[log batch][ticket={}][{}] recursive query collect log files result nums out, vmLogs is empty", ticket, logBatchType());
                    return null;
                }
                this.vmLogsFromWs(host, protocol, username, password, port, vmLogsUri, title, times, ticket);
            }
            Optional<VmLogDTO> first = vmLogs.stream().filter(log -> title.equals(log.getTitle())).findFirst();
            if (!first.isPresent()) {
                if (times > maxRecursiveNum) {
                    log.info("[log batch][ticket={}][{}] recursive query collect log files result nums out, vmLog is not present", ticket, logBatchType());
                    return null;
                }
                this.vmLogsFromWs(host, protocol, username, password, port, vmLogsUri, title, times, ticket);
            }
            VmLogDTO vmLogDTO = first.get();
            boolean vmLogStatus = vmLogDTO.getStatus().equals(0);
            log.info("[log batch][ticket={}][{}] recursive query collect log files completes is {}", ticket, logBatchType(), !vmLogStatus);
            if (vmLogStatus) {
                return this.vmLogsFromWs(host, protocol, username, password, port, vmLogsUri, title, times, ticket);
            }
            return vmLogDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LogBatchCollectorTypeEnum logBatchType() {
        return LogBatchCollectorTypeEnum.workspace_domain;
    }
}
