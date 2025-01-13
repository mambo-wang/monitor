package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.WatcherWarnDTO;
import org.springframework.boot.ApplicationArguments;

import java.util.List;


public interface WatcherWarnMgrApi {
    /**
     * 获取采集端告警信息
     * @param deployId 节点id
     * @param type 告警类型
     * @return
     */
    WatcherWarnDTO queryWatcherWarnByDeployIdAndType(String deployId, String type);

    /**
     * 添加或者修改采集端告警信息
     * @param deployId 节点id
     * @param warnName 告警名称
     * @param level 告警级别
     * @param message 告警信息
     * @param lastTime 最新告警时间
     * @param type 告警类型
     * @param firstTime 首次告警时间
     */
    void editWatcherWarnByDeployIdAndType(String deployId, String warnName,Integer level,String message,Long lastTime,String type,Long firstTime);

//    //搜集采集端CPU利用率、内存利用率、根路径磁盘利用率
//    List<WatcherWarnDTO> collectWatcherCpuRate();
//    List<WatcherWarnDTO> collectWatcherMemRate();
//    List<WatcherWarnDTO> collectWatcherStorageUsage();

}
