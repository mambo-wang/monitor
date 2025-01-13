package com.virtual.cloud.om.cas.service.log;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.token.cas.CasTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.CasHostInfoDTO;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.logBatch.GatherLogDto;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.CasRsTaskMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasLogCollector implements LogBatchCollector {
    private final CasTokenRestConnection casTokenRestConnection;
    private final CasRestConnection casRestConnection;


    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();
        GatherLogDto dto = new GatherLogDto();
        dto.setSize(100);
        dto.setTime(time);
        dto.setTargetHost(true);
        //获取CAS可用的主机列表，筛选存在待收集日志的hostId。因为传错误的hostId会导致CAS的日志收集任务一直停在错误的hostId日志收集任务上，导致日志收集无法正常使用。
        List<String> inputHostIdList = Arrays.stream(targets).map(LogBatchTargetsQueryDTO::getId).collect(Collectors.toList());
        RpcResult<List<CasHostInfoDTO>> rpcResult = casTokenRestConnection.get(host, protocol, username, password, port, CasUriConstants.Host.QUERY_HOST_LIST, new ParameterizedTypeReference<RpcResult<List<CasHostInfoDTO>>>() {
        }).getBody();
        List<String> allAvailableHostIdList = rpcResult.getData().stream().map(CasHostInfoDTO::getId).collect(Collectors.toList());
        List<String> availableInputHostIdList = inputHostIdList.stream().filter(hostId -> allAvailableHostIdList.contains(hostId)).collect(Collectors.toList());
        List<String> illegalInputHostIDList = inputHostIdList.stream().filter(hostId -> !allAvailableHostIdList.contains(hostId)).collect(Collectors.toList());
        illegalInputHostIDList.forEach(illegalHostId -> {
            log.error("[log batch][ticket={}][{}] Host with hostId {} does not exist!", ticket, logBatchType(), illegalHostId);
        });
        if (availableInputHostIdList.size() == 0) {
            log.info("[log batch][ticket={}][{}] collect log files ! time={}ms", ticket, this.logBatchType(), (System.currentTimeMillis() - startTime));
            return DownloadResultEnum.fail;
        }
        dto.setHosts(availableInputHostIdList);
        // 收集日志。CAS的日志收集开启后返回的data字段，可以做收集任务查询接口中的msgId字段来查询收集是否完成
        Boolean inQueue = true;
        RpcResult<String> result = null;
        //若任务已存在则需等待
        while (inQueue) {
            result = this.casTokenRestConnection.put(host, protocol, username, password, port, CasUriConstants.Log.GATHER_LOG, JSONUtil.toJsonStr(dto), new ParameterizedTypeReference<RpcResult<String>>() {
            }).getBody();
            inQueue = !result.isSuccess();
            if (inQueue) {
                try {
                    // 每 10s 轮询一次收集结果
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }
        String msgId = result.getData();
        boolean collectingFinished = false;
        while (!collectingFinished) {
            String queryUri = String.format(CasUriConstants.MESSAGE, msgId);
            CasRsTaskMsg casRsTaskMsg = this.casRestConnection.get(platform, host, protocol, port, username, password, queryUri, new ParameterizedTypeReference<CasRsTaskMsg>() {
            });
            collectingFinished = casRsTaskMsg.getCompleted();
            if (!collectingFinished) {
                try {
                    // 每 10s 轮询一次收集结果
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        //CAS日志收集文件下载
        String uuid = msgId;
        try {
            log.info("[log batch][ticket={}][{}] download log file start!", ticket, logBatchType());
            this.casTokenRestConnection.downloadBigFile(host, protocol, username, password, port, String.format(CasUriConstants.Log.DOWNLOAD_LOGFILE, uuid), logFileDirPath, logBatchType().name(), ".tar.gz");
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
        return LogBatchCollectorTypeEnum.cas_host;
    }


}
