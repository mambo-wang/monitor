package com.virtual.cloud.om.sdk.constant.onestor.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Onestor 存储池 监控信息字段枚举类
 * 需传入 响应资源名称 使用
 * */
@AllArgsConstructor
@Getter
public enum OneStorStorageMonitorTargetEnum {
    iops_read("onestor.%s.pool.%s.iops.rd", "存储写IOPS"),
    iops_write("onestor. %s.pool.%s.iops.wr", "存储读IOPS"),

    storage_read_bw("servers.%s.diskstat.root.bw.rd", "存储读带宽"),
    storage_write_bw("servers.%s.diskstat.root.bw.wr", "存储写带宽"),
    onestor_pool_iops_all("onestor.%s.pool.%s.iops.all", "存储写带宽"),
    onestor_pool_bw_all("onestor.%s.pool.%s.bw.all", "存储写带宽"),

    ;
    private final String value;
    private final String desc;
}
