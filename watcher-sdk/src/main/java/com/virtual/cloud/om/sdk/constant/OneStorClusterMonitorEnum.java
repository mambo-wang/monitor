package com.virtual.cloud.om.sdk.constant;

import lombok.Getter;

/**
 * @author:XK
 * @Date:2022/8/24 17:54
 */
@Getter
public enum OneStorClusterMonitorEnum {

    pool_all_iops_rd("big_cluster.pool.all.iops.rd","存储读IOPS"),
    pool_all_iops_wr("big_cluster.pool.all.iops.wr","存储写IOPS"),
    big_cluster_recovery_ops("big_cluster.recovery.ops","存储恢复OPS"),
    big_cluster_fs_ops_rd("big_cluster.fs.ops.rd","文件读OPS"),
    big_cluster_fs_ops_wr("big_cluster.fs.ops.wr","文件写OPS"),
    big_cluster_pool_all_bw_rd("big_cluster.pool.all.bw.rd","存储读带宽"),
    big_cluster_pool_all_bw_wr("big_cluster.pool.all.bw.wr","存储写带宽"),
    big_cluster_recovery_bw("big_cluster.recovery.bw","存储恢复带宽"),
    big_cluster_fs_bw_rd("big_cluster.fs.bw.rd","文件读带宽"),
    big_cluster_fs_bw_wr("big_cluster.fs.bw.wr","文件写带宽"),
    big_cluster_pool_all_data_rd("big_cluster.pool.all.data.rd","存储读流量"),
    big_cluster_pool_all_data_wr("big_cluster.pool.all.data.wr","存储写流量"),
    big_cluster_pool_all_data_rc("big_cluster.pool.all.data.rc","存储恢复流量"),

    big_cluster_fs_data_rd("big_cluster.fs.data.rd","文件读流量"),
    big_cluster_fs_data_wr("big_cluster.fs.data.wr","文件写流量"),
    big_cluster_space_total("big_cluster.space.total","总容量"),
    big_cluster_space_used("big_cluster.space.used","已用容量"),
    big_cluster_cpu_ratio("big_cluster.cpu.ratio","cpu占用率"),
    big_cluster_mem_ratio("big_cluster.mem.ratio","内存占用率"),
    onestor_diskstat_lat_rd("onestor.*.diskstat.lat.rd","数据盘读时延"),
    onestor_diskstat_lat_wr("onestor.*.diskstat.lat.wr","数据盘写时延"),
    big_cluster_diskstat_util_avg("big_cluster.diskstat.util.avg","数据盘平均负载"),
    big_cluster_diskstat_util_max("big_cluster.diskstat.util.max","数据盘最大负载"),

    big_cluster_fs_bw_total("big_cluster.fs.bw.total ","文件总带宽"),
    big_cluster_pool_all_iops_all("big_cluster.pool.all.iops.all","集群总IOPS"),
    big_cluster_fs_ops_total("big_cluster.fs.ops.total","文件总OPS"),
    big_cluster_pool_all_bw_all("big_cluster.pool.all.bw.all","集群总带宽"),

    server_loadavg_1("servers.%s.loadavg_1","集群总带宽"),
    server_loadavg_5("servers.%s.loadavg_5","集群总带宽"),
    server_loadavg_15("servers.%s.loadavg_15","集群总带宽"),

    network_all_rx_byte("servers.%s.network.all.rx_byte","網卡接收"),
    network_all_tx_byte("servers.%s.network.all.tx_byte","網卡發送"),
    network_all_rx_packets("servers.%s.network.all.rx_packets","集群总带宽"),
    network_all_rx_drop("servers.%s.network.all.rx_drop","集群总带宽"),

    network_all_rx_errors("servers.%snetwork.all.rx_errors","集群总带宽"),
    network_all_tx_packets("servers.%s.network.all.tx_packets","集群总带宽"),
    network_all_tx_drop("servers.%s.network.all.tx_drop","集群总带宽"),

    network_all_tx_errors("servers.%s.network.all.tx_errors","集群总带宽"),
    sub_cluster_node_pool_all_bw_all("sub_cluster.node.pool.all.bw.all","集群总带宽"),
    sub_cluster_node_fs_bw_total("sub_cluster.node.fs.bw.total","集群总带宽");

    private final String value;
    private final String desc;

    OneStorClusterMonitorEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
