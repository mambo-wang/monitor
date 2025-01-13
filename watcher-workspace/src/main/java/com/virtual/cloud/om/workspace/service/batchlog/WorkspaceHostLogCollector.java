package com.virtual.cloud.om.workspace.service.batchlog;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.logBatch.GatherLogNewDto;
import com.virtual.cloud.om.sdk.dto.logBatch.GatherLogTreeDto;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.dto.logBatch.OperationLogResultDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceHostLogCollector implements LogBatchCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();
        GatherLogNewDto dto = new GatherLogNewDto();
        dto.setSize(100);
        dto.setTime(Objects.isNull(time) ? 1 : time);
        dto.setTargetHost(true);
        List<GatherLogTreeDto> treeDtos = Stream.of(targets).map(target -> {
            GatherLogTreeDto treeDto = new GatherLogTreeDto();
            treeDto.setId(Long.valueOf(target.getId()));
            return treeDto;
        }).collect(Collectors.toList());
        dto.setGatherLogTreeDto(treeDtos);
        // 收集日志
        RpcResult<String> gatherRpc = this.wsTokenRestConnection.post(host, protocol, username, password, port, WsUriConstants.GATHER_LOG, JSONUtil.toJsonStr(dto), new ParameterizedTypeReference<RpcResult<String>>() {
        }).getBody();
        Utils.checkResult(WsUriConstants.GATHER_LOG, gatherRpc);
        log.info("[log batch][ticket={}][{}] query log files success!", ticket, logBatchType());
        String uuid = gatherRpc.getData();
        RpcResult<OperationLogResultDTO> resultRpc = this.wsTokenRestConnection.get(host, protocol, username, password, port, WsUriConstants.GATHER_LOG_RESULT, new ParameterizedTypeReference<RpcResult<OperationLogResultDTO>>() {
        }).getBody();
        Utils.checkResult(WsUriConstants.GATHER_LOG_RESULT, resultRpc);
        OperationLogResultDTO resultDto = resultRpc.getData();
        log.info("[log batch][ticket={}][{}] query log files collect result ready download is {}", ticket, logBatchType(), resultDto.getIsReadyDownload().equals(0));
        // 收集结果1表示没有下载文件
        while (resultDto.getIsReadyDownload().equals(1)) {
            resultRpc = this.wsTokenRestConnection.get(host, protocol, username, password, port, WsUriConstants.GATHER_LOG_RESULT, new ParameterizedTypeReference<RpcResult<OperationLogResultDTO>>() {
            }).getBody();
            Utils.checkResult(WsUriConstants.GATHER_LOG_RESULT, resultRpc);
            log.info("[log batch][ticket={}][{}] query log files collect result ready download is {}", ticket, logBatchType(), resultDto.getIsReadyDownload().equals(0));
            resultDto = resultRpc.getData();
            try {
                // 每 10s 轮询一次收集结果
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("[log batch][ticket={}][{}] query log files collect result ready download is {}", ticket, logBatchType(), resultDto.getIsReadyDownload().equals(0));
        if (!resultDto.getRandomUuid().equals(uuid)) {
            // TODO uuid不一致
            // 按理说不会发生这种情况，预留一下，遇到了再处理吧
        }
        try {
            log.info("[log batch][ticket={}][{}] download log file start!", ticket, logBatchType());
            // 下载收集文件
            this.wsTokenRestConnection.downloadBigFile(host, protocol, username, password, port, String.format(WsUriConstants.GATHER_LOG_DOWNLOAD, uuid), logFileDirPath, logBatchType().name(), ".zip");
            log.info("[log batch][ticket={}][{}] download log file success!", ticket, logBatchType());
        } catch (IOException e) {
            e.printStackTrace();
            return DownloadResultEnum.fail;
        }
        log.info("[log batch][ticket={}][{}] collect log files ! time={}ms", ticket, this.logBatchType(), (System.currentTimeMillis() - startTime));
        return DownloadResultEnum.success;
    }

    @Override
    public LogBatchCollectorTypeEnum logBatchType() {
        return LogBatchCollectorTypeEnum.workspace_host;
    }
}
