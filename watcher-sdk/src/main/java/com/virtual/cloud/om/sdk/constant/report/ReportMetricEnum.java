package com.virtual.cloud.om.sdk.constant.report;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum ReportMetricEnum {
    desktop_pool_basic("桌面池基本信息上报",true),
    desktop_pool_vm_relation("桌面池与虚拟机映射关系",true),
    terminal_basic("终端基本信息上报",true),
    host_basic("主机基本信息",true),
    cluster_basic("集群基本信息",true),
    domain_basic("虚拟机基本信息",true),
    storage_pool_basic("存储池基本信息",true),
    storage_volume_basic("存储卷基本信息",true),
    share_file_basic("共享文件基本信息",true),
    share_file_host_basic("使用共享存储的主机基本信息",true),
    resource_plat_version("资源平台版本号", true),
    watcher_basic_info("采集端基本信息", true),
    stor_nodepool_basic("onestor节点池基本数据采集", true),
    stor_host_basic("onestor主机基本数据采集", true),
    stor_diskpool_basic("onestor硬盘池基本数据采集", true),
    stor_storage_pool_basic("onestor存储池基本数据采集", true),
    stor_disk_basic("ONESTOR硬盘基本信息上报", true),
    stor_host_nic("nic网卡", false),
    stor_host_sys_avg_load("系统平均负载", false),
    stor_host_disk_load("disk_load", false),
    diskpool_capacity("diskpool_capaciy", false),

    host_cpu_usage("主机CPU利用率",false),
    mem_usage("内存利用率",false),
    disk_iops("磁盘IOPS",false),
    disk_latency("查询磁盘读写延迟",false),
    disk_usage("磁盘利用率",false),
    net_throughput("网络吞吐量",false),
    partition_usage("分区利用率",false),
    host_cpu_TopN("查询主机下虚拟机cpu_topN信息",false),
    host_mem_TopN("查询主机下虚拟机内存topN信息",false),
    host_performance("查询主机当前的性能数据",false),
    host_summary("查询主机当前的概要信息",false),
    host_overview("查询指定主机的概要信息",false),
    cpu_allocate_rate("主机cpu分配比",false),
    mem_allocate_rate("主机内存分配比",false),
    cluster_host_top5_cpu_usage("查询集群下主机cpu_topN信息",false),
    cluster_host_top5_mem_usage("查询集群下主机内存topN信息",false),
    cluster_virt_host_cpu_TopN("查询集群下虚拟机cpu_topN信息",false),
    cluster_virt_host_mem_TopN("查询集群下虚拟机内存topN信息",false),
    cpu_usage("cpu利用率",false),
    disk_rate("存储利用率",false),
    connection("虚拟机连接数",false),
    cpu_usage_detail("cpu使用情况",false),
    disk_throughput("磁盘IO吞吐量",false),
    health_info("主机健康度信息",false),
    resource_user_number("资源用户数量",false),
    operation_log("虚拟机任务操作日志", false),
    watcher_cpu_usage("采集端cpu利用率", false),
    watcher_mem_usage("采集端内存利用率", false),
    watcher_disk_usage("采集端根路径磁盘利用率", false),
    watcher_component_cpu_usage("采集端服务cpu利用率", false),
    watcher_component_mem_usage("采集端服务内存利用率", false),


    stor_cluster_basic("stor集群基本信息",true),
    stor_cluster_monitor("stor集群监控信息",true),
    stor_cluster_pg("stor_pg信息",true),
    stor_cluster_capacity_basic("stor_capacity_basic信息",true),
    stor_cluster_rbd_capacity("stor_rbd_capacity信息",true),
    stor_cluster_fs_capacity("stor_fs_capacity信息",true),
    stor_cluster_rgw_capacity("stor_rgw_capacity信息",true),
    stor_cluster_iops("stor_集群iops信息",false),
    stor_cluster_bandwidth("stor_集群bandwidth",false),
    stor_cluster_flow("stor_集群flow",false),
    stor_cluster_capacity("stor_集群flow",false),
    stor_cluster_cpu_usage("stor_集群cpu_usage",false),
    stor_cluster_mem_usage("stor_集群mem_usage",false),
    stor_cluster_disk_delay("stor_集群disk_delay",false),
    stor_cluster_disk_load("stor_集群disk_load",false),
    node_iops("onestor节点池监控数据IOPS数据采集", false),
    node_bandwidth("onestor节点池监控数据bandwidth数据采集", false),
    node_flow("onestor节点池监控数据flow数据采集", false),
    node_capacity("onestor节点池监控数据capacity数据采集", false),
    node_cpu_usage("onestor节点池监控数据cpu_usage数据采集", false),
    node_mem_usage("onestor节点池监控数据mem_usage数据采集", false),
    node_disk_delay("onestor节点池监控数据disk_delay数据采集",false),
    node_disk_load("onestor节点池监控数据disk_load数据采集", false),

    storage_iops("onestor存储池监控数据IOPS数据采集", false),
    storage_bandwidth("onestor存储池监控数据bandwidth数据采集", false),

    diskpool_iops("onestor硬盘池监控数据IOPS数据采集", false),
    diskpool_bandwidth("onestor硬盘池监控数据bandwidth数据采集", false),

    host_iops("onestor主机监控数据IOPS数据采集", false),
    host_bandwidth("onestor主机监控数据bandwidth数据采集", false),
    host_capacity("onestor主机监控数据capacity数据采集", false),
    host_mem_usage("onestor主机监控数据mem_usage数据采集", false),
    host_disk_delay("onestor主机监控数据disk_delay数据采集", false),
    host_disk_load("onestor主机监控数据disk_load数据采集", false),

    stor_host_cpu_usage("onestor主机cpu",false),
    stor_host_mem_usage("onestor主机mem",false)
    ;

    public final String desc;
    public final Boolean staticMetric;

    public static List<ReportMetricEnum> getStaticMetrics(){
        return Arrays.stream(values()).filter(e->e.staticMetric).collect(Collectors.toList());
    }
}
