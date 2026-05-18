package com.virtual.cloud.om.sdk.constant.report;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    watcher_component_mem_usage("采集端服务内存利用率", false)
    ;

    public final String desc;
    public final Boolean staticMetric;

    ReportMetricEnum(String desc, Boolean staticMetric) {
        this.desc = desc;
        this.staticMetric = staticMetric;
    }

    public static List<ReportMetricEnum> getStaticMetrics(){
        return Arrays.stream(values()).filter(e->e.staticMetric).collect(Collectors.toList());
    }
}
