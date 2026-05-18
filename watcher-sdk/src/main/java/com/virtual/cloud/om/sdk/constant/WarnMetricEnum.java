package com.virtual.cloud.om.sdk.constant;

public enum WarnMetricEnum {
    ws_realtime_alarms("ws实时告警"),
    cas_realtime_alarms("cas实时告警"),
    uis_realtime_alarms("uis实时告警"),
    terminal_alarms("终端告警"),
    vipdesktop_alarms("VIP桌面告警"),
    watcher_cpu_warn("采集端CPU利用率告警"),
    watcher_mem_warn("采集端内存利用率告警"),
    watcher_storage_warn("采集端根路径磁盘利用率告警")
    ;

    public final String desc;

    WarnMetricEnum(String desc) {
        this.desc = desc;
    }
}
