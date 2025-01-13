package com.virtual.cloud.om.sdk.constant.onestor.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Onestor 硬盘池 监控信息字段枚举类
 * 需传入 响应资源名称 使用
 * */
@AllArgsConstructor
@Getter
public enum OneStorDiskPoolMonitorTargetEnum {
    iops_read("diskpool.%s.iops.wr", "存储写IOPS"),
    iops_write("diskpool.%s.iops.rd", "存储读IOPS"),

    storage_read_bw("diskpool.%s.bw.rd", "存储读带宽"),
    storage_write_bw("diskpool.%s.bw.wr", "存储写带宽"),
    storage_recover_bw("diskpool.%s.bw.rc", "存储恢复带宽"),

    capacity_total("diskpool.%s.space.total", "总容量"),
    capacity_used("diskpool.%s.space.used", "已用容量"),

    ;
    private final String value;
    private final String desc;
}
