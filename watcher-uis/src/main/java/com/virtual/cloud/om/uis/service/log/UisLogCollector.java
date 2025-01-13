package com.virtual.cloud.om.uis.service.log;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.logBatch.GatherLogDto;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UisLogCollector implements LogBatchCollector {
    private final UisRestConnection uisRestConnection;

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();
        GatherLogDto dto = new GatherLogDto();
        dto.setSize(100);
        dto.setTime(time);
        dto.setTargetHost(true);
        dto.setHosts(Arrays.stream(targets).map(LogBatchTargetsQueryDTO::getId).collect(Collectors.toList()));
        RpcResult result = this.uisRestConnection.put(host, protocol, username, password, port, UisUriConstants.Log.GATHER_LOG, JSONUtil.toJsonStr(dto), new ParameterizedTypeReference<RpcResult>() {
        }).getBody();
        Boolean startCollect = result.isSuccess();//判断当前任务是否开始收集
        while (!startCollect){
            try {
                // 每 10s 轮询一次收集结果
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("[log batch][ticket={}][{}] old version 轮询日志收集结果", ticket, this.logBatchType());
            result = this.uisRestConnection.put(host, protocol, username, password, port, UisUriConstants.Log.GATHER_LOG, JSONUtil.toJsonStr(dto), new ParameterizedTypeReference<RpcResult>() {
            }).getBody();
            startCollect = result.isSuccess();
        }
        // 新版本uis的逻辑，日志收集接口变为了异步，会直接返回调用成功并且data字段中传回了任务id，可以通过任务id查询任务状态
        while (startCollect && Objects.nonNull(result.getData()) && !String.valueOf(result.getData()).startsWith("[")) {
            try {
                // 每 10s 轮询一次收集结果
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("[log batch][ticket={}][{}] new version 轮询日志收集结果", ticket, this.logBatchType());
            UisQueryMessageDTO msgDto = this.uisRestConnection.get(host, protocol, username, password, port,
                    String.format(UisUriConstants.Log.MESSAGE_QUERY, result.getData()), new ParameterizedTypeReference<UisQueryMessageDTO>() {
                    }).getBody();
            if(msgDto.getProgress().equals(100L)&& Objects.nonNull(msgDto.getComplete())){
                break;
            }
        }
        //UIS日志收集
        try {
            log.info("[log batch][ticket={}][{}] download log file start!", ticket, logBatchType());
            this.uisRestConnection.downloadBigFile(host, protocol, username, password, port, UisUriConstants.Log.DOWNLOAD_LOGFILE, logFileDirPath, logBatchType().name(), ".tar.gz");
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
        return LogBatchCollectorTypeEnum.uis_host;
    }

    /**
     * 查询控制台任务接口的返回dto
     */
    @Data
    static class UisQueryMessageDTO{
        private Long id;
        private String name;
        private String target;
        private Long progress;
        private String detail;
        private String user;
        private String address;
        private Long reqStart;
        private Long start;
        private Long complete;
        private Boolean balloon;
        private Long result;
//        private Long refreshData;
//        private Long keyValue;
//        private String backupInfo;
        private Long eventType;
        private Long targetId;
        private Boolean failed;
    }
}
