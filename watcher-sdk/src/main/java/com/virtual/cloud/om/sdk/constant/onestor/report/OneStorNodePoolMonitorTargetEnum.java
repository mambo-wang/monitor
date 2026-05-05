package com.virtual.cloud.om.sdk.constant.onestor.report;

/**
 * Onestor 节点池 监控信息字段枚举类
 * 需传入 响应资源名称 使用
 * */
public enum OneStorNodePoolMonitorTargetEnum {
    iops_read("sub_cluster.%s.pool.all.iops.wr", "存储写IOPS"),
    iops_write("sub_cluster.%s.pool.all.iops.rd", "存储读IOPS"),
    ops_recover("sub_cluster.%s.recovery.ops", "存储恢复OPS"),
    ops_read("sub_cluster.%s.fs.ops.rd", "文件读OPS"),
    ops_write("sub_cluster.%s.fs.ops.wr", "文件写OPS"),

    storage_read_bw("sub_cluster.%s.pool.all.bw.rd", "存储读带宽"),
    storage_write_bw("sub_cluster.%s.pool.all.bw.wr", "存储写带宽"),
    storage_recover_bw("sub_cluster.%s.recovery.bw", "存储恢复带宽"),
    fs_read_bw("sub_cluster.%s.fs.bw.rd", "文件读带宽"),
    fs_write_bw("sub_cluster.%s.fs.bw.wr", "文件写带宽"),

    storage_read_flow("sub_cluster.%s.pool.all.data.rd","存储读流量"),
    storage_write_flow("sub_cluster.%s.pool.all.data.wr","存储写流量"),
    storage_recover_flow("sub_cluster.%s.pool.all.data.rc","存储恢复流量"),
    fs_read_flow("sub_cluster.%s.fs.data.rd","文件读流量"),
    fs_write_flow("sub_cluster.%s.fs.data.wr","文件写流量"),

    capacity_total("sub_cluster.%s.space.total", "总容量"),
    capacity_used("sub_cluster.%s.space.used", "已用容量"),
    sub_cluster_node_cpu_ratio("sub_cluster.%s.cpu.ratio", "已用容量"),
    sub_cluster_node_mem_ratio("sub_cluster.%s.cpu.ratio", "已用容量"),

    lat_read("sub_cluster.%s.diskstat.lat.rd", "数据盘读时延"),
    lat_wite("sub_cluster.%s.diskstat.lat.wr", "数据盘写时延"),

    util_avg("sub_cluster.%s.diskstat.util.avg", "数据盘平均负载"),
    util_max("sub_cluster.%s.diskstat.util.max", "数据盘最大负载"),
    ;
    private final String value;
    private final String desc;

    OneStorNodePoolMonitorTargetEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() { return value; }
    public String getDesc() { return desc; }
}
