package com.virtual.cloud.om.sdk.constant;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * tip：注释说明中 ReportMetricEnum 简称为metric，DataReportTypeByMetricEnum 简称为type
 * 这个类是为了适配同一条策略多个资源平台使用的情况
 * 1. 枚举类name可以自定义，和数据中心下发的metric不是一回事，目前大部分与[ReportMetricEnum]中重名是因为方便省事
 * 2. DataReportTypeByMetricEnum.metric 字段是对应数据中心下发的metric，因为同一条策略有可能涉及到由多个资源平台的实现类来完成，相当于db中的外键[一(metric)对多(type)]
 * 3. DataReportTypeByMetricEnum.platform 字段是实现类对应的资源平台类型：
 * DataReportTypeByMetricEnum.metric + DataReportTypeByMetricEnum.platform 相当于db中的唯一索引
 * 如果DataReportTypeByMetricEnum.metric 唯一，DataReportTypeByMetricEnum.platform 值可以为null
 * 如果DataReportTypeByMetricEnum.metric 不唯一，请指定 DataReportTypeByMetricEnum.platform 的值，否则会获取到多个type
 */
@SuppressWarnings("all")
public enum DataReportTypeByMetricEnum {
    desktop_pool_basic("桌面池基本信息上报", ReportMetricEnum.desktop_pool_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),
    desktop_pool_vm_relation("桌面池与虚拟机映射关系", ReportMetricEnum.desktop_pool_vm_relation, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),
    terminal_basic("终端基本信息上报", ReportMetricEnum.terminal_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),
    diskpool_capacity("终端基本信息上报", ReportMetricEnum.diskpool_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),

//    host_cpu_usage("主机CPU利用率", ReportMetricEnum.host_cpu_usage, null,null),
    host_mem_usage("内存利用率", ReportMetricEnum.mem_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_mem_usage("内存利用率", ReportMetricEnum.mem_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    cluster_mem_usage("内存利用率", ReportMetricEnum.mem_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.cluster),
    cluster_disk_iops("磁盘IOPS", ReportMetricEnum.disk_iops, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.cluster),
    host_disk_iops("磁盘IOPS", ReportMetricEnum.disk_iops, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_disk_iops("磁盘IOPS", ReportMetricEnum.disk_iops, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    host_disk_latency("查询磁盘读写延迟", ReportMetricEnum.disk_latency, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_disk_latency("查询磁盘读写延迟", ReportMetricEnum.disk_latency, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),

    mem_usage("内存利用率", ReportMetricEnum.mem_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    disk_iops("磁盘IOPS", ReportMetricEnum.disk_iops, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    disk_latency("查询磁盘读写延迟", ReportMetricEnum.disk_latency, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),


    host_disk_usage("磁盘利用率", ReportMetricEnum.disk_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_disk_usage("磁盘利用率", ReportMetricEnum.disk_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    host_net_throughput("网络吞吐量", ReportMetricEnum.net_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_net_throughput("网络吞吐量", ReportMetricEnum.net_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    host_partition_usage("分区利用率", ReportMetricEnum.partition_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_partition_usage("分区利用率", ReportMetricEnum.partition_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),


    disk_usage("磁盘利用率", ReportMetricEnum.disk_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    net_throughput("网络吞吐量", ReportMetricEnum.net_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    partition_usage("分区利用率", ReportMetricEnum.partition_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    host_basic("主机基本信息", ReportMetricEnum.host_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    host_cpu_TopN("查询主机下虚拟机cpu_topN信息", ReportMetricEnum.host_cpu_TopN, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    host_mem_TopN("查询主机下虚拟机内存topN信息", ReportMetricEnum.host_mem_TopN, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    host_performance("查询主机当前的性能数据", ReportMetricEnum.host_performance, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    host_summary("查询主机当前的概要信息", ReportMetricEnum.host_summary, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    host_overview("查询指定主机的概要信息", ReportMetricEnum.host_overview, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cpu_allocate_rate("主机cpu分配比", ReportMetricEnum.cpu_allocate_rate, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    mem_allocate_rate("主机内存分配比", ReportMetricEnum.mem_allocate_rate, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),

    cluster_basic("集群基本信息", ReportMetricEnum.cluster_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    domain_basic("虚拟机基本信息", ReportMetricEnum.domain_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    storage_pool_basic("存储池基本信息", ReportMetricEnum.storage_pool_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    storage_volume_basic("存储卷基本信息", ReportMetricEnum.storage_volume_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    share_file_basic("共享文件基本信息", ReportMetricEnum.share_file_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    share_file_host_basic("使用共享存储的主机基本信息", ReportMetricEnum.share_file_host_basic, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    cluster_host_top5_cpu_usage("查询集群下主机cpu_topN信息", ReportMetricEnum.cluster_host_top5_cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cluster_host_top5_mem_usage("查询集群下主机内存topN信息", ReportMetricEnum.cluster_host_top5_mem_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cluster_virt_host_cpu_TopN("查询集群下虚拟机cpu_topN信息", ReportMetricEnum.cluster_virt_host_cpu_TopN, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cluster_virt_host_mem_TopN("查询集群下虚拟机内存topN信息", ReportMetricEnum.cluster_virt_host_mem_TopN, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cluster_cpu_usage("cpu利用率", ReportMetricEnum.cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},ReportSeparateEnum.cluster),
    host_cpu_usage("cpu利用率", ReportMetricEnum.cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_cpu_usage("cpu利用率", ReportMetricEnum.cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    host_disk_throughput("磁盘IO吞吐量", ReportMetricEnum.disk_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.host),
    domain_disk_throughput("磁盘IO吞吐量", ReportMetricEnum.disk_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.domain),
    cluster_disk_throughput("磁盘IO吞吐量", ReportMetricEnum.disk_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},ReportSeparateEnum.cluster),

    cpu_usage("cpu利用率", ReportMetricEnum.cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis},null),
    disk_rate("存储利用率", ReportMetricEnum.disk_rate, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    connection("虚拟机连接数", ReportMetricEnum.connection, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cpu_usage_detail("cpu使用情况", ReportMetricEnum.cpu_usage_detail, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    disk_throughput("磁盘IO吞吐量", ReportMetricEnum.disk_throughput, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),

    health_info("主机健康度信息", ReportMetricEnum.health_info, new ReportResourceEnum[]{ReportResourceEnum.workspace, ReportResourceEnum.cas, ReportResourceEnum.uis, ReportResourceEnum.onestor},null),
    cas_resource_user_number("cas资源用户数量", ReportMetricEnum.resource_user_number, new ReportResourceEnum[]{ReportResourceEnum.cas},null),
    uis_resource_user_number("uis资源用户数量", ReportMetricEnum.resource_user_number, new ReportResourceEnum[]{ReportResourceEnum.uis},null),
    workspace_resource_user_number("workspace资源用户数量", ReportMetricEnum.resource_user_number, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),

    cas_operation_log("cas虚拟机任务操作日志", ReportMetricEnum.operation_log, new ReportResourceEnum[]{ReportResourceEnum.cas},null),
    uis_operation_log("uis虚拟机任务操作日志", ReportMetricEnum.operation_log, new ReportResourceEnum[]{ReportResourceEnum.uis},null),
    workspace_operation_log("workspace虚拟机任务操作日志", ReportMetricEnum.operation_log, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),


    workspace_resource_plat_version("workspace资源平台版本号", ReportMetricEnum.resource_plat_version, new ReportResourceEnum[]{ReportResourceEnum.workspace},null),
    uis_resource_plat_version("uis资源平台版本号", ReportMetricEnum.resource_plat_version, new ReportResourceEnum[]{ReportResourceEnum.uis},null),
    cas_resource_plat_version("cas资源平台版本号", ReportMetricEnum.resource_plat_version, new ReportResourceEnum[]{ReportResourceEnum.cas},null),

    watcher_basic_info("采集端基本信息", ReportMetricEnum.watcher_basic_info, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),
    watcher_cpu_usage("采集端cpu利用率", ReportMetricEnum.watcher_cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),
    watcher_mem_usage("采集端内存利用率", ReportMetricEnum.watcher_mem_usage, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),
    watcher_disk_usage("采集端根路径磁盘利用率", ReportMetricEnum.watcher_disk_usage, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),
    watcher_component_cpu_usage("采集端服务cpu利用率", ReportMetricEnum.watcher_component_cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),
    watcher_component_mem_usage("采集端服务内存利用率", ReportMetricEnum.watcher_component_mem_usage, new ReportResourceEnum[]{ReportResourceEnum.hccAgent},null),

    stor_cluster_basic("oneStor集群基本信息", ReportMetricEnum.stor_cluster_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_monitor("oneStor集群监控信息", ReportMetricEnum.stor_cluster_monitor, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_pg("oneStor_pg信息", ReportMetricEnum.stor_cluster_pg, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_capacity_basic("oneStor_capacity基本信息", ReportMetricEnum.stor_cluster_capacity_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_rbd_capacity("oneStor_rbd_capacity基本信息", ReportMetricEnum.stor_cluster_rbd_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_fs_capacity("oneStor_fs_capacity基本信息", ReportMetricEnum.stor_cluster_fs_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_rgw_capacity("oneStor_rgw_capacity基本信息", ReportMetricEnum.stor_cluster_rgw_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_iops("oneStor集群iops", ReportMetricEnum.stor_cluster_iops, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_bandwidth("oneStor集群bandwidth", ReportMetricEnum.stor_cluster_bandwidth, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_flow("oneStor集群flow", ReportMetricEnum.stor_cluster_flow, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_capacity("oneStor集群capacity", ReportMetricEnum.stor_cluster_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_cpu_usage("oneStor集群capacity", ReportMetricEnum.stor_cluster_cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_mem_usage("oneStor集群mem_usage", ReportMetricEnum.stor_cluster_mem_usage, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_disk_delay("oneStor集群mem_usage", ReportMetricEnum.stor_cluster_disk_delay, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_cluster_disk_load("oneStor集群mem_usage", ReportMetricEnum.stor_cluster_disk_load, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),


    stor_nodepool_basic("onestor节点池基本数据采集", ReportMetricEnum.stor_nodepool_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_host_basic("onestor主机基本数据采集", ReportMetricEnum.stor_host_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_diskpool_basic("onestor硬盘池基本数据采集", ReportMetricEnum.stor_diskpool_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_storage_pool_basic("onestor存储池基本数据采集", ReportMetricEnum.stor_storage_pool_basic, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),

    node_iops("onestor节点池监控数据IOPS数据采集", ReportMetricEnum.node_iops, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_bandwidth("onestor节点池监控数据bandwidth数据采集", ReportMetricEnum.node_bandwidth, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_flow("onestor节点池监控数据flow数据采集", ReportMetricEnum.node_flow, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_capacity("onestor节点池监控数据capacity数据采集", ReportMetricEnum.node_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_cpu_usage("onestor节点池监控数据cpu_usage数据采集", ReportMetricEnum.node_cpu_usage, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_mem_usage("onestor节点池监控数据mem_usage数据采集", ReportMetricEnum.node_mem_usage, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_disk_delay("onestor节点池监控数据disk_delay数据采集", ReportMetricEnum.node_disk_delay, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    node_disk_load("onestor节点池监控数据disk_load数据采集", ReportMetricEnum.node_disk_load, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),

    storage_iops("onestor存储池监控数据IOPS数据采集", ReportMetricEnum.storage_iops, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    storage_bandwidth("onestor存储池监控数据bandwidth数据采集", ReportMetricEnum.storage_bandwidth, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),

    diskpool_iops("onestor硬盘池监控数据IOPS数据采集", ReportMetricEnum.diskpool_iops, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    diskpool_bandwidth("onestor硬盘池监控数据bandwidth数据采集", ReportMetricEnum.diskpool_bandwidth, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),

    host_iops("onestor主机监控数据IOPS数据采集", ReportMetricEnum.host_iops, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    host_bandwidth("onestor主机监控数据bandwidth数据采集", ReportMetricEnum.host_bandwidth, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    host_capacity("onestor主机监控数据capacity数据采集", ReportMetricEnum.host_capacity, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    host_disk_delay("onestor主机监控数据disk_delay数据采集", ReportMetricEnum.host_disk_delay, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    host_disk_load("onestor主机监控数据disk_load数据采集", ReportMetricEnum.host_disk_load, new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_host_cpu_usage("onestor主机cpu",ReportMetricEnum.stor_host_cpu_usage,new ReportResourceEnum[]{ReportResourceEnum.onestor},ReportSeparateEnum.host),
    stor_host_mem_usage("onestor主机cpu",ReportMetricEnum.stor_host_mem_usage,new ReportResourceEnum[]{ReportResourceEnum.onestor},ReportSeparateEnum.host),

    stor_disk_basic("onestor硬盘基本信息",ReportMetricEnum.stor_disk_basic,new ReportResourceEnum[]{ReportResourceEnum.onestor},null),
    stor_host_nic("onestor硬盘基本信息",ReportMetricEnum.stor_host_nic,new ReportResourceEnum[]{ReportResourceEnum.onestor},ReportSeparateEnum.host),
    stor_host_sys_avg_load("sysAvgLoad系统平均负载",ReportMetricEnum.stor_host_sys_avg_load,new ReportResourceEnum[]{ReportResourceEnum.onestor},ReportSeparateEnum.host),
    stor_host_disk_load("disk_load",ReportMetricEnum.stor_host_disk_load,new ReportResourceEnum[]{ReportResourceEnum.onestor},ReportSeparateEnum.host)
    ;

    public final String desc;
    public final ReportMetricEnum metric;
    public final ReportResourceEnum[] platform;
    public final ReportSeparateEnum separate;

    DataReportTypeByMetricEnum(String desc, ReportMetricEnum metric, ReportResourceEnum[] platform, ReportSeparateEnum separate) {
        this.desc = desc;
        this.metric = metric;
        this.platform = platform;
        this.separate = separate;
    }

    /**
     * 根据策略和平台获取 数据上报实现类的类型
     * 策略和平台相当于唯一索引
     *
     * @param metric
     * @param platform
     * @return
     */
    public static List<DataReportTypeByMetricEnum> getTypesByMetricAndPlatform(ReportMetricEnum metric, ReportResourceEnum platform) {
        return Arrays.stream(values()).filter(e -> e.metric == metric && Arrays.stream(e.platform).filter(p -> p == platform).findFirst().isPresent()).collect(Collectors.toList());
    }
    public static List<DataReportTypeByMetricEnum> getTypesByMetricAndSeparate(ReportMetricEnum metric, ReportSeparateEnum separate) {
        // 根据策略和平台获取唯一一个type
        Optional<DataReportTypeByMetricEnum> first = Arrays.stream(values()).filter(e -> e.metric == metric && e.separate == separate).findFirst();
        // 如果这个type不存在，则返回策略对应的所有type
        // 这样再同一个策略的前提下，既可以兼容指定实现类也可以兼容多个实现类
        return first.isPresent() ? Lists.newArrayList(first.get()) : Arrays.stream(values()).filter(e -> e.metric == metric).collect(Collectors.toList());
    }


}
