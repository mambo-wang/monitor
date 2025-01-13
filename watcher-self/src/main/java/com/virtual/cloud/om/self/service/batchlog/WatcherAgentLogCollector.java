package com.virtual.cloud.om.self.service.batchlog;

import cn.hutool.core.lang.UUID;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatcherAgentLogCollector implements LogBatchCollector {

    @Override
    public DownloadResultEnum download(String platform, String protocol, String host, Integer port, String username, String password, Integer time, String logFileDirPath, String ticket, LogBatchTargetsQueryDTO... targets) {
        log.info("[log batch][ticket={}][{}] collect log files start!", ticket, this.logBatchType());
        long startTime = System.currentTimeMillis();

        SSHHost sshHost = SSHHost.newInstance(host, username, password);
        dealLogs(sshHost, logFileDirPath, time);

        log.info("[log batch][ticket={}][{}] collect log files ! time={}ms", ticket, this.logBatchType(), (System.currentTimeMillis() - startTime));
        return DownloadResultEnum.success;
    }

    @Override
    public LogBatchCollectorTypeEnum logBatchType() {
        return LogBatchCollectorTypeEnum.agent;
    }




    private void dealLogs(SSHHost sshHost, String filePath, int time){
        String logPath = getLogsPath(sshHost);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dirName = String.format("watcher-%s-log-dir", this.logBatchType().name());
        if(dirName.length() > 0){
            SSHTools.rmFile(sshHost, logPath + "/" + dirName + "*");
        }
        dirName = dirName + "-" + sshHost.getIp() + "-" + UUID.randomUUID().toString().replace("-", "");
        SSHTools.executeNoException(sshHost, String.format("mkdir %s/%s", logPath, dirName));
        //1、将今日日志格式化压缩
        String defaultLogName = "watcher-agent.log";
        //要复制今日日志、转化名称为符合格式的日志名称
        String formatLogName = String.format("watcher-agent%s.log", sdf.format(date));
        SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                assemblePath(logPath, defaultLogName),
                assemblePath(logPath, dirName, formatLogName)));
        SSHTools.executeNoException(sshHost, String.format("gzip %s", assemblePath(logPath, dirName, formatLogName)));
        //2、将其他日期格合并压缩卷
        if(time < 1){
            //查询全部
            //移动到目标文件夹中
            SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                    assemblePath(logPath, "watcher-agent*.log.gz"),
                    assemblePath(logPath, dirName)));
        }else{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            for (int i = 0; i < time - 1; i++) {
                calendar.add(calendar.DATE, -1);
                Date currentDate = calendar.getTime();
                //获取往日日志
                String sourceFormatLogName = String.format("watcher-agent%s.*.log.gz", sdf.format(currentDate));
                SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                        assemblePath(logPath, sourceFormatLogName),
                        assemblePath(logPath, dirName)));
            }
        }
        //3、将所有的压缩包打包，获取压缩包
        String zipName = dirName  + ".tar.gz";
        SSHTools.executeNoException(sshHost, String.format("tar -zcvf %s -C %s",
                assemblePath(logPath, zipName),//要压缩成为的压缩包
                assemblePath(logPath) + " " + dirName));//原本文件
        SSHTools.executeNoException(sshHost, String.format("mkdir %s", filePath));
        SSHTools.copyFileFromRemote(assemblePath(filePath, zipName), assemblePath(logPath, zipName), sshHost);
        log.info("[watcher-agent-log] download file {} to {} successfully", zipName, filePath);
        //3、删除当前的临时文件
        SSHTools.rmFile(sshHost, assemblePath(logPath, dirName) + "*");
    }


    private String getLogsPath(SSHHost sshHost) {
        String watcherHome = SSHTools.executeNoException(sshHost, "cat /etc/watcher_home");
        watcherHome = watcherHome.replace("\n", "");
        return assemblePath(watcherHome, "logs");
    }

    private String assemblePath(String prefix, String ...path){
        StringBuffer sb = new StringBuffer(prefix);
        if(sb.charAt(sb.length() - 1) == '/'){
            sb.deleteCharAt(sb.length() - 1);
        }
        Arrays.stream(path).forEach(currentPath -> sb.append("/" + currentPath));
        return sb.toString();
    }

}
