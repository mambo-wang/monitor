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
import com.virtual.cloud.om.sdk.dto.logBatch.TerminalLogDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceTerminalLogCollector implements LogBatchCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();
        String logDirPath = new StringBuilder(logFileDirPath).append(logFileDirPath.endsWith(File.separator) ? Strings.EMPTY : File.separator).append(logBatchType()).toString();
        List<TerminalLogDTO> list = Lists.newCopyOnWriteArrayList();
        CompletableFuture[] completableFutures = Arrays.stream(targets).map(target ->
                CompletableFuture.supplyAsync(() -> {
                    final String id = target.getId();
                    final String collectUri = String.format(WsUriConstants.TERMINAL_LOG_COLLECT, id);
                    // 收集日志
                    RpcResult<String> rpcCollect = this.wsTokenRestConnection.post(host, protocol, username, password, port, collectUri, "", new ParameterizedTypeReference<RpcResult<String>>() {
                    }).getBody();
                    try {
                        Utils.checkResult(collectUri, rpcCollect);
                    } catch (AppException e) {
                        log.error("[log batch][ticket={}][{}] collect log files apply error :{}", e);
                        return null;
                    }
                    log.info("[log batch][ticket={}][{}] collect log files apply success!", ticket, logBatchType());
                    final String vmLogsUri = WsUriConstants.TERMINAL_LOGS;
                    // 查询收集结果
                    List<TerminalLogDTO> logs = this.logsFromWs(host, protocol, username, password, port, vmLogsUri, 1, ticket);
                    return logs;
                }).whenCompleteAsync((result, tr) -> {
                    if (CollUtil.isNotEmpty(result)) {
                        list.addAll(result);
                    }
                })
        ).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        // 将重名日志收集记录 分组
        Map<String, List<TerminalLogDTO>> map = list.stream().collect(Collectors.groupingBy(TerminalLogDTO::getFileName));
        list.clear();
        // 根据日志收集名称去重
        map.forEach((k, v) -> {
            if (CollUtil.isNotEmpty(v)) {
                list.add(v.stream().findFirst().get());
            }
        });
        list.stream().filter(logDto -> logDto.getSuccNum().equals(1)).forEach(logDto -> {
            try {
                final String fileName = logDto.getFileName();
                // 下载收集文件
                log.info("[log batch][ticket={}][{}] download log file start!", ticket, logBatchType());
                this.wsTokenRestConnection.downloadBigFile(host, protocol, username, password, port,
                        String.format(WsUriConstants.TERMINAL_LOG_DOWNLOAD, logDto.getId()),
                        logDirPath, fileName.substring(0, fileName.lastIndexOf(".")), ".zip");
                log.info("[log batch][ticket={}][{}] download log file success!", ticket, logBatchType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        log.info("[log batch][ticket={}][{}] collect log files ! time={}ms", ticket, this.logBatchType(), (System.currentTimeMillis() - startTime));
        Optional<TerminalLogDTO> success = list.stream().filter(logDto -> logDto.getSuccNum().equals(1)).findFirst();
        Optional<TerminalLogDTO> fail = list.stream().filter(logDto -> logDto.getSuccNum().equals(0)).findFirst();
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
     * @param logsUri
     * @param times
     * @param ticket
     * @return 如果此方法内部发生异常，算作失败，返回null
     */
    private List<TerminalLogDTO> logsFromWs(String host, String protocol, String username, String password, Integer port, String logsUri, Integer times, String ticket) {
        try {
            final int maxRecursiveNum = 10;
            String logsRpcStr = this.wsTokenRestConnection.get(host, protocol, username, password, port, logsUri, new ParameterizedTypeReference<String>() {
            }).getBody();
            RpcResult logsRpc = JSONUtil.toBean(logsRpcStr, RpcResult.class);
            Utils.checkResult(logsUri, logsRpc);
            try {
                log.info("[log batch][ticket={}][{}] recursive query collect log files result, thread sleep", ticket, logBatchType());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            List<TerminalLogDTO> logs = JSONUtil.toList(JSONUtil.parseObj(logsRpc.getData()).getStr("data"), TerminalLogDTO.class);
            if (CollUtil.isEmpty(logs)) {
                if (times > maxRecursiveNum) {
                    log.info("[log batch][ticket={}][{}] recursive query collect log files result nums out, logs is empty", ticket, logBatchType());
                    return null;
                }
                this.logsFromWs(host, protocol, username, password, port, logsUri, times, ticket);
            }
            Optional<TerminalLogDTO> first = logs.stream().filter(tLog -> Objects.isNull(tLog.getSuccNum())).findFirst();
            if (first.isPresent()) {
                if (times > maxRecursiveNum) {
                    log.info("[log batch][ticket={}][{}] recursive query collect log files result nums out, succNum field null is present", ticket, logBatchType());
                    return null;
                }
                this.logsFromWs(host, protocol, username, password, port, logsUri, times, ticket);
            }
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LogBatchCollectorTypeEnum logBatchType() {
        return LogBatchCollectorTypeEnum.workspace_terminal;
    }
}
