package com.virtual.cloud.om.onestor.service.log;

import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.api.ResourceApi;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.GatherOneStorLogDto;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnestorLogCollector implements LogBatchCollector {

    private final OnestorRestConnection onestorRestConnection;
    private final ResourceApi resourceApi;

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Long endTime = date.getTime();
        calendar.add(Calendar.DATE, -1 * time);
        Long startTime = calendar.getTime().getTime();

        GatherOneStorLogDto gatherOneStorLogDto = new GatherOneStorLogDto();
        gatherOneStorLogDto.setReserved(false);
        gatherOneStorLogDto.setLanguage("zh-CN");
        gatherOneStorLogDto.setModuleHistory(Arrays.asList("BLOCK", "NAS", "CLUSTER_STORAGE", "CLUSTER_MANAGEMENT", "HANDY", "OS", "MAINTENANCE_MANAGEMENT", "CONFIG_INFO", "OBJECT"));
        gatherOneStorLogDto.setNodes(Arrays.stream(targets).map(LogBatchTargetsQueryDTO::getId).collect(Collectors.toList()));
        gatherOneStorLogDto.setStartTime(startTime/1000);
        gatherOneStorLogDto.setEndTime(endTime/1000);
        try {
            log.info("[log batch][ticket={}][{}] download log file start!", ticket, logBatchType());
            String gatherLogUrl = String.format(OnestoreUriConstants.Log.ONESTOR_SYSTEM_LOG);
            Boolean startCollect = false;
            OneStorRestResult oneStorRestResult = null;
            while(!startCollect){
                oneStorRestResult = onestorRestConnection.post(host, protocol, username, password, port, gatherLogUrl, SerializeUtils.toJson(gatherOneStorLogDto), new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
                List<Object> result = oneStorRestResult.getResult();
                Integer status = (Integer)result.get(0);
                 if(status.equals(0)){//此值说明收集完成,收集时长大约为4min
                    startCollect = true;
                }else if(status.equals(-10009)){//此值说明已有日志收集任务
                    startCollect = false;
                    try {
                        // 每 10s 尝试再次收集
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }else{
                    log.error("[log batch][ticket={}][{}] download log file error!", ticket, logBatchType());
                    return DownloadResultEnum.fail;
                }
            }
            String zipFileRemotePath = ((LinkedHashMap)oneStorRestResult.getData()).get("file_name").toString();
            String[] zipFileRemotePaths = zipFileRemotePath.split("/temp/");
            String targetDownloadPath = zipFileRemotePaths[1];
            String downloadUrl = String.format(OnestoreUriConstants.Log.DOWNLOAD_LOG_FILE, targetDownloadPath);
            String targetFilePath = logFileDirPath + "/" + logBatchType().name() + ".tar.gz" ;
            onestorRestConnection.downloadBigFile(host, protocol, username, password, port, downloadUrl, targetFilePath, logBatchType().name(), ".tar.gz");
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
        return LogBatchCollectorTypeEnum.onestor_host;
    }
}
