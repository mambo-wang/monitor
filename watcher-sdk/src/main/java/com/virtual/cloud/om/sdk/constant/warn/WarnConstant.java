package com.virtual.cloud.om.sdk.constant.warn;

/**
 * @Author: zkf9688
 * @Date: 2022/9/13 10:25
 */
public interface WarnConstant {

    interface eventSrc {
        String HOST_SRC = "主机";
        String DOMAIN_SRC = "虚拟机";
        String CLUSTER_SRC = "集群";
        String DESK_SRC = "桌面";
        String VIRTUAL_APP_SRC = "虚拟应用";
        String TERMINAL_SRC = "终端";
        String DISTRIBUTE_STORAGE_SRC = "分布式存储";
    }

    interface ObjectType {
        Integer CLUSTER_OBJECTTYPE = 1;
        Integer HOST_OBJECTTYPE = 2;
        Integer DOMAIN_OBJECTTYPE = 3;
        Integer TERMINAL_OBJECTTYPE = 4;
        Integer WATCHER_OBJECTTYPE = 5;
        Integer VIRTUAL_APP_OBJECTTYPE = 6;
        Integer DISTRIBUTE_STORAGE_OBJECTTYPE = 7;
        Integer DESK_OBJECTTYPE = 8;
        Integer VIP_DESK_OBJECTTYPE = 9;
        Integer DEVICE_OBJECTTYPE = 10;
        Integer NAS_OBJECTTYPE = 11;
        Integer NET_OBJECTTYPE = 12;
        Integer OS_OBJECTTYPE = 13;
        Integer OTHER_OBJECTTYPE = 14;
    }

    interface count {
        //采集端,onestor告警,workspace终端告警重复数量
        Integer WARN_COUNT = 1;
    }

    interface eventName {
        String CPU_USAGE_NAME = "能力中心Agent CPU利用率告警";
        String MEM_USAGE_NAME = "能力中心Agent内存利用率告警";
        String STORAGE_USAGE_NAME = "能力中心Agent根路径磁盘利用率告警";
        //onestor告警名称使用告警模块+资源告警
        String ONESTOR_WARN_NAME = "资源告警";
        //VIP桌面告警
        String VIP_DESK_WARN_NAME = "VIP桌面告警";
        //终端异常告警
        String TERMINAL_WARN_NAME = "终端异常告警";
    }

    interface WatcherWarn{
        /**
         * 告警类型
         * 能力中心Agent资源告警：801
         */
        Integer WATCHER_WARN_TYPE = 801;

        /**
         * 告警等级：
         * 紧急：1
         * 严重：2
         * 次要：3
         * 警告：4
         */
        Integer WARN_LEVEL_URGENT = 1;
        Integer WARN_LEVEL_SERIOUS = 2;

        /**
         * 能力中心Agent告警
         */
        Integer WARN_BASIC_COUNT = 90;
        Double WARN_URGENT_COUNT = 100.0;
    }

    interface warnType {
        Integer VIP_DESK_WARN_TYPE = 601;
        Integer TERMINAL_WARN_TYPE = 602;
    }

    interface warnLevel {
        /**
         * 告警等级：VIP桌面告警+终端告警的告警级别
         * 紧急：1
         * 严重：2
         * 次要：3
         * 警告：4
         */
        Integer WARN_LEVEL_WARNING = 4;
    }
}
