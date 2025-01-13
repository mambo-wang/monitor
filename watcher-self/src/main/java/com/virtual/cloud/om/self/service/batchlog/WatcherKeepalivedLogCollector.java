package com.virtual.cloud.om.self.service.batchlog;

import cn.hutool.core.lang.UUID;
import com.virtual.cloud.om.sdk.api.LogBatchCollector;
import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatcherKeepalivedLogCollector implements LogBatchCollector {

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
        return LogBatchCollectorTypeEnum.keepalived;
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
        //复制到需要的日志文件目标文件夹中
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                assemblePath(logPath, "messages"),
                assemblePath(logPath, dirName, "messages-" + sdf.format(calendar.getTime()).replace("-", ""))));
        if(time < 1){
            //time < 1查询所有的日志
            SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                    assemblePath(logPath, "messages-*"),
                    assemblePath(logPath, dirName)));
        }else{
            for (int i = 0; i < time - 1; i++) {
                calendar.add(calendar.DATE, -1);
                Date currentDate = calendar.getTime();
                //移动到目标文件夹中
                SSHTools.executeNoException(sshHost, String.format("cp %s %s ",
                        assemblePath(logPath, "messages-" + sdf.format(currentDate).replace("-", "")),
                        assemblePath(logPath, dirName)));
            }
        }
        //将目标文件夹打包，获取压缩包
        String zipName = dirName + ".tar.gz";
        SSHTools.executeNoException(sshHost, String.format("tar -zcvf %s -C %s",
                assemblePath(logPath, zipName),//要压缩成为的压缩包
                assemblePath(logPath) + " " + dirName));//原本文件
        SSHTools.executeNoException(sshHost, String.format("mkdir %s", filePath));
        SSHTools.copyFileFromRemote(assemblePath(filePath, zipName), assemblePath(logPath, zipName), sshHost);
        log.info("[watcher-agent-log] download file {} to {} successfully", zipName, filePath);
        //删除当前的临时文件
        SSHTools.rmFile(sshHost, assemblePath(logPath, dirName) + "*");
    }


    private String getLogsPath(SSHHost sshHost) {
        return "/var/log";
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
