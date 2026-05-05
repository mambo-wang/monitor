package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.constant.LogBatchCollectorTypeEnum;
import com.virtual.cloud.om.sdk.dto.logBatch.LogBatchTargetsQueryDTO;

public interface LogBatchCollector {
    /**
     * 收集下载日志
     *
     * @return
     */
    DownloadResultEnum download(String platform,String protocol, String host, Integer port, String username, String password, Integer time,String logFileDirPath,String ticket, LogBatchTargetsQueryDTO... targets);

    /**
     * 日志类型
     * @return
     */
    LogBatchCollectorTypeEnum logBatchType();

    enum DownloadResultEnum{
        success(0),
        part_success(1),
        fail(2),
        ;

        public final Integer val;

        DownloadResultEnum(Integer val) {
            this.val = val;
        }
    }
}
