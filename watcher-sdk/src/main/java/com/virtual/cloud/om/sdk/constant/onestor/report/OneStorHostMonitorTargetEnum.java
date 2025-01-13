package com.virtual.cloud.om.sdk.constant.onestor.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Onestor 主机 监控信息字段枚举类
 * 需传入 响应资源名称 使用
 * */
@AllArgsConstructor
@Getter
public enum OneStorHostMonitorTargetEnum {
    iops_read("servers.%s.diskstat.root.iops.wr", "存储写IOPS"),
    iops_write("servers.%s.diskstat.root.iops.rd", "存储读IOPS"),
    ops_read("servers.%s.fs.ops.rd", "文件读OPS"),
    ops_write("servers.%s.fs.ops.wr", "文件写OPS"),

    storage_read_bw("servers.%s.diskstat.root.bw.rd", "存储读带宽"),
    storage_write_bw("servers.%s.diskstat.root.bw.wr", "存储写带宽"),
    fs_read_bw("sub_cluster.%s.fs.bw.rd", "文件读带宽"),
    fs_write_bw("sub_cluster.%s.fs.bw.wr", "文件写带宽"),

    capacity_total("servers.%s.diskspace.root.total", "总容量"),
    capacity_used("servers.%s.diskspace.root.used", "已用容量"),

    cpu_usage("servers.%s.cpu.usage.ratio", "CPU占用率"),
    mem_usage("servers.%s.mem.ratio","内存占用率"),

    lat_read("servers.%s.diskstat.root.lat.rd", "数据盘读时延"),
    lat_wite("servers.%s.diskstat.root.lat.wr", "数据盘写时延"),

    //此值只有存储节点才会有
    util_avg("servers.%s.diskstat.util.avg", "数据盘平均负载"),
    util_max("servers.%s.diskstat.util.max", "数据盘最大负载"),
    ;
    private final String value;
    private final String desc;
}
