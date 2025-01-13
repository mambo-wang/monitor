package com.virtual.cloud.om.sdk.constant.uri;

/**
 * @Author: w22798
 * @Date: 2022/5/05 14:36
 */
@SuppressWarnings("all")
public interface CasUriConstants {

    String CAS_NAME = "/cas/casrs";

    String MESSAGE = CAS_NAME + "/message/%s";
    String QUERY_PARAMETER = CAS_NAME + "/parameter/query?type=%s";
    String ADD_PARAMETER = CAS_NAME + "/parameter/add?type=%s";
    String TEST_CONNECTION = CAS_NAME + "/operator/test";
    String HOST_WAKE = CAS_NAME + "/host/wake";
    String HOST_SHUTDOWN = CAS_NAME + "/host/shutoff";
    String HOST_RESTART = CAS_NAME + "/host/reboot";
    String HOST_INTO = CAS_NAME + "/host/into";
    String HOST_EXIT = CAS_NAME + "/host/exit";
    String PLATFORM_VERSION = CAS_NAME + "/version";

    String LOGIN = "/cas/spring_check?name=%s&password=%s";

    /**
     * 概览
     */
    interface Reports {
        String DOMAIN_DISK_UTILIZATION_TOPN = CAS_NAME + "/reports/vm/disk/utilizationTop?topNum=%s";
        String HOST_DISK_UTILIZATION_TOPN = CAS_NAME + "/reports/cluster/%s/hostTopNDiskUtilization";
        String QUERY_VIRT_HOST_INFO = "/cas/cluster/queryVirtHostInfo?limit=%s&offset=%s";
        String QUERY_CLUSTER_LIST = "/cas/cluster/queryClusterList?limit=%s&offset=%s";
        String CPU_MEM_VM = "/cas/report/cpuMemVm?startTime=%s&endTime=%s&cycle=%s&domainId=%s&type=%s";
        String DISK_VM = "/cas/report/diskVm?startTime=%s&endTime=%s&cycle=%s&domainId=%s&type=%s";
        String NET_VM = "/cas/report/netVm?startTime=%s&endTime=%s&cycle=%s&domainId=%s&type=%s";
        String NET_SP_VM = "/cas/report/netSpVm?startTime=%s&endTime=%s&cycle=%s&domainId=%s&type=%s";
        String CPU_MEM_TOP = "/cas/report/cpuMemTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&type=%s";
        String CPU_MEM_VM_TOP = "/cas/report/cpuMemVmTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&hostId=%s&type=%s";
        String NET_TOP = "/cas/report/netTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&type=%s";
        String NET_VM_TOP = "/cas/report/netVmTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&hostId=%s&type=%s";
        String IO_TOP = "/cas/report/ioTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&type=%s";
        String IO_VM_TOP = "/cas/report/ioVmTop?startTime=%s&endTime=%s&topNum=%s&clusterId=%s&hostId=%s&type=%s";
        String IO_VM = "/cas/report/ioVm?startTime=%s&endTime=%s&cycle=%s&domainId=%s";
        String GET_CLOUD_HOST = "/cas/tree/getCloudHostList";
        String VM_RESOURCE_REPORT = "/cas/dc/vmResourceReport?t=%s";
        String IP_RESOURCE_REPORT = "/cas/domain/queryIpResourceList?offset=%s&limit=%s";
        String IP_RESOURCE_REPORT_IP = "/cas/domain/queryIpResourceList?offset=%s&limit=%s&ip=%s";
        String VLAN_RESOURCE_REPORT = "/cas/domain/queryVlanResourceList?limit=%s&offset=%s&sortDir=%s&sortField=%s";
        String VLAN_RESOURCE_REPORT_VLAN = "/cas/domain/queryVlanResourceList?limit=%s&offset=%s&sortDir=%s&sortField=%s&vlanId=%s";
        String VM_DISK_REPORT = "/cas/storage/vmDiskInfos?offset=%s&limit=%s";
        String BLOCK_INFO = "/cas/storage/blockInfos?offset=%s&limit=%s";
        String SHARE_FILE_SYS_INFO = "/cas/storage/shareFileSysInfos?offset=%s&limit=%s";
        String RBD_POOLS = "/cas/rbd/rbdPools?offset=%s&limit=%s";
        String HOST_CONDITION = "/cas/host/condition?offset=%s&limit=%s";
        String VM_DISK_INFO = "/cas/domain/queryDomainDiskInfo?domainId=%s";
        String RBD_VMLIST = "/cas/rbd/pool/vmList?cephUuid=%s&name=%s";
        String SF_VMLIST = "/cas/storage/sf/vmList?id=%s";
        String VM_PROCESS_LIST = "/cas/domain/%s/processList?id=%s&limit=%s&offset=%s";
    }


    /**
     * 集群
     */
    interface Cluster {
        String CLUSTER_ADD = CAS_NAME + "/cluster/add";
        String CLUSTER_QUERY_BY_ID = CAS_NAME + "/cluster/%s";
        String CLUSTER_QUERY_ALL = CAS_NAME + "/cluster/clusters";
        // 查询集群下的虚拟机交换机
        String QUERY_CLUSTER_VSWITCH = CAS_NAME + "/vswitch?clusterId=%s";

        String CLUSTER_QUERY_DETAIL = CAS_NAME + "/cluster/summary/%s";
        String CLUSTER_QUERY_BY_POOLID = "/cas/cluster/queryClusterByHostPoolId?hostPoolId=";
        String QUERY_CLUSTER_SHARE_FILE = "/cas/storage/cluster/%s/shareFile";
        String QUERY_CLUSTER_HOST_CPU_TOPN = "/cas/casrs/cluster/hostTopN/%s/cpu";
        String QUERY_CLUSTER_HOST_MEM_TOPN = "/cas/casrs/cluster/hostTopN/%s/memory";
        String QUERY_CLUSTER_VIRT_HOST_CPU_TOPN = "/cas/casrs/cluster/virtHostTopN/%s/cpu";
        String QUERY_CLUSTER_VIRT_HOST_MEM_TOPN = "/cas/casrs/cluster/virtHostTopN/%s/mem";

        /**
         * 集群cpu利用率
         */
        String QUERY_CLUSTER_CPU_USAGE = "/cas/casrs/cluster/avgHostTrend/%s/cpu/30";
        /**
         * 集群内存利用率
         */
        String QUERY_CLUSTER_MEM_USAGE = "/cas/casrs/cluster/avgHostTrend/%s/mem/30";
        /**
         * 集群磁盘吞吐量
         */
        String QUERY_CLUSTER_DISK_IOPS = "/cas/casrs/cluster/diskIOPSTrend/%s/30";

    }

    /**
     * 主机
     */
    interface Host {
        /**
         * 查询主机信息
         */
        String HOST_BASIC_INFO_ALL = CAS_NAME + "/host";
        String HOST_BASIC_INFO = CAS_NAME + "/host/id/%s";
        /**
         * 查询集群-主机信息
         */
        String CLUSTER_HOST_ALL = "/cas/host/queryHostList";
        /**
         * 查询存储路径
         */
        String TEMPLATE_STORAGE_LIST = "/cas/template/templateStorageList";

        // 查询主机下的虚拟交换机
        String QUERY_HOST_VSWITCH = CAS_NAME + "/host/id/%s/vswitch";
        String QUERY_HOST_VSWITCH_FORM = "/cas/network/host/%s/busAndManage/vswitchs";

        //主机添加入集群cas
        String HOST_ADDHOST_URL = "/cas/casrs/host/add";
        String HOST_HEALTH = "/cas/casrs/dashboard/health";

        String HOST_ALL_INFO = CAS_NAME + "/host/list";
        String HOST_MONITOR_INFO = CAS_NAME + "/host/id/%s/monitor";
        /**
         * 连接主机
         **/
        String CONNECT_HOST = "/cas/casrs/host/connect/v2";

        /**
         * 查询模板存储池
         **/
        String QUERY_TEMPLATE_STORAGE_POOL = "/cas/casrs/vmTemplate/templateStoragePoolList";

        String CREATE_TEMPLATE_STORAGE_POOL = "/cas/casrs/vmTemplate/templateStorage";

        /**
         * 根据主机id或集群id查询主机或集群虚拟交换机名称列表
         */
        String QUERY_VSWITCH_NAME = CAS_NAME + "/vm/queryVSwtichNameList/%s/%s";

        String QUERY_HOST_LIST = "/cas/host/queryHostList";

        String QUERY_HOST_USAGE = "/cas/dc/host?offset=0&limit=1000";
//	/cas/casrs/host/overview/{id}
        String QUERY_HOST_BY_HOST_ID = CAS_NAME + "/host/overview/%s";
        String QUERY_HOST_TOP_CPU_TOPN = "/cas/casrs/host/%s/top/domain?type=cpu";
        String QUERY_HOST_TOP_MEM_TOPN = "/cas/casrs/host/%s/top/domain?type=memory";
        String QUERY_HOST_PERFORMANCE = "/cas/casrs/host/%s/realtime";
        String QUERY_HOST_SUMMARY = "/cas/casrs/host/summary/%s";
        String QUERY_HOST_OVERVIEW = "/cas/casrs/host/overview/%s";

        /**
         * 服务器下性能监控 cpu利用率
         */
        String QUERY_HOST_CPU_USAGE = "/cas/casrs/host/%s/cpuTrend";
        String QUERY_HOST_MEM_USAGE = "/cas/casrs/host/%s/memTrend";
        String QUERY_HOST_IO_IOPS = "/cas/casrs/host/%s/ioRwTrend";
        /**
         * 网络吞吐量
         */
        String QUERY_HOST_NET_IOPS = "/cas/casrs/host/%s/netRwTrend";
        /**
         * 磁盘请求
         */
        String QUERY_HOST_DISKS_IOPS = "/cas/casrs/host/%s/diskIOPSTrend";
        /**
         * 主机磁盘利用率
         */
        String QUERY_HOST_DISKS_USAGE = "/cas/casrs/host/%s/diskRateTrend";

        /**
         * 主机分区利用率
         */
        String QUERY_HOST_PARTITION_USAGE = "/cas/casrs/host/%s/partitionList";
        String QUERY_HOST_DISK_LATENCY = "/cas/casrs/host/%s/diskLatencyTrend";

        /**
         * 获取健康度信息
         */
        String QUERY_HOST_DASHBOARD_HEALTH = "/cas/casrs/dashboard/health";
        String QUERY_HOST_MESSAGE = "/cas/casrs/host/list";



    }

    /**
     * 主机池
     */
    interface HostPool {
        /**
         * 查询所有主机池id
         **/
        String QUERY_ALL_HOST_URL = "/cas/casrs/hostpool/all";

        /**
         * 查询主机池共享文件
         **/
        String QUERY_HOSTPOOL_SHAREFILE = "/cas/casrs/hostpool/shareFile/%s";

        /**
         * 连接主机池下所有主机
         **/
        String CONNECT_HOSTPOOL = "/cas/casrs/hostpool/connectAllHost/%s";
    }

    /**
     * 虚拟机
     */
    interface Domain {
        String DOMAIN = CAS_NAME + "/vm";

        /**
         * 申请mac
         **/
        String DOMAIN_MAC_GET = DOMAIN + "/mac";

        /**
         * 查询虚拟机信息
         */
        String DOMAIN_BASIC_INFO = DOMAIN + "/%s";
        /**
         * 查询虚拟机信息
         */
        String DOMAIN_BASIC_INFO_BY_UUID = DOMAIN + "/uuid/%s";
        //查询虚拟机详细信息
        String DOMAIN_DETAIL_INFO = DOMAIN + "/detail/uuid/%s";

        //查询虚拟机详细信息
        String DOMAIN_DETAIL_INFO_BY_ID = DOMAIN + "/detail/%s";
        /**
         * 增加虚拟机
         */
        String DOMAIN_ADD = DOMAIN + "/add";
        /**
         * 删除虚拟机
         */
        String DOMAIN_DELETE = DOMAIN + "/delete/uuid/%s?type=%s";
        /**
         * 删除虚拟机,同时删除base文件
         */
        String DOMAIN_DELETE_WITH_BASE = DOMAIN + "/delete/uuid/%s?type=%s&delBase0=true";
        /**
         * 部署虚拟机
         */
        String DOMAIN_DEPLOY = DOMAIN + "/deploy";
        /**
         * 虚拟机快照
         */
        String DOMAIN_SNAPSHOT = DOMAIN + "/snapshot";
        /**
         * 虚拟机快照查询
         */
        String DOMAIN_SNAPSHOT_QUERY = DOMAIN_SNAPSHOT + "/uuid/%s";
        /**
         * 虚拟机快照还原
         */
        String DOMAIN_SNAPSHOT_RESUME = DOMAIN_SNAPSHOT + "/resume";
        /**
         * 删除指定虚拟机的快照
         */
        String DOMAIN_SNAPSHOT_DELETE = DOMAIN + "/snapshot/uuid/%s/%s";
        /**
         * 批量删除虚拟机快照
         */
        String DOMAIN_SNAPSHOTS_DELETE = DOMAIN + "/uuid/%s/snapshot";
        /**
         * （条件）查询所有虚拟机
         */
        String DOMAIN_QUERY_BY_CONDITION = DOMAIN + "/vmList";
        /**
         * 查询所有虚拟机（列表）
         */
        String DOMAIN_QUERY_ALL = DOMAIN + "/vmList";
        /**
         * 查询所有虚拟机（列表）
         */
        String DOMAIN_QUERY_BY_CLUSTER_ID = DOMAIN + "/vmList";
        /**
         * 根据条件查询所有符合条件的虚拟机
         */
        String DOMAIN_QUERY = DOMAIN + "/vmList?hpId=%d";
        /**
         * 根据虚拟机id查询虚拟机
         */
        String DOMAIN_QUERY_BY_ID = DOMAIN + "/%d";
        /**
         * 根据虚拟机uuid查询虚拟机
         */
        String DOMAIN_QUERY_BY_UUID = DOMAIN + "/uuid/%s";
        /**
         * 运行指定ID的虚拟机
         */
        String DOMAIN_START = DOMAIN + "/start/%s";
        /**
         * 关闭指定ID的虚拟机
         */
        String DOMAIN_STOP = DOMAIN + "/stop/%s";
        /**
         * 断电虚拟机
         */
        String DOMAIN_SHUT_DOWN = DOMAIN + "/powerOff/%s";
        /**
         * 重启指定ID的虚拟机
         */
        String DOMAIN_RESTART = DOMAIN + "/restart/%s";
        /**
         * 批量重启虚拟机
         */
        String DOMAIN_BATCH_RESTART = "/cas/domain/batch/restart";
        /**
         * 批量关机虚拟机
         */
        String DOMAIN_BATCH_SHUTDOWN = "/cas/domain/batch/shutdown";
        /**
         * 批量启动虚拟机
         */
        String DOMAIN_BATCH_START = "/cas/domain/batch/start";
        /**
         * 关闭指定ID的虚拟机电源
         */
        String DOMAIN_POWEROFF = DOMAIN + "/powerOff/%s";
        /**
         * 为指定虚拟机添加虚拟硬盘
         */
        String DOMAIN_ADD_DISK = DOMAIN + "/addDevice";
        /**
         * 卸载虚拟机的虚拟磁盘，包括光驱、软驱
         */
        String DOMAIN_DELETE_DISK = DOMAIN + "/delDevice";
        String DOMAIN_ADD_VIDEO_CARD = DOMAIN + "/addDevice";
        String DOMAIN_DELETE_VIDEO_CARD = DOMAIN + "/delDevice";
        /**
         * 检查虚拟机名称是否已经存在
         **/
        String DOMAIN_NAME_DUPLICATION_CHECK = CAS_NAME + "/vm/isExist?name=%s";
        /**
         * 删除虚拟机模板
         **/
        String DOMAIN_DELETE_VM_TEMPLATE = CAS_NAME + "/vmTemplate/deleteVmTemplate?id=%s";
        /**
         * 创建虚拟机模板
         **/
        String DOMAIN_CREATE_VM_TEMPLATE = CAS_NAME + "/vmTemplate/addVmTemplate";
        /**
         * 创建虚拟机模板
         **/
        String DOMAIN_MOVE_VM_TEMPLATE = CAS_NAME + "/vmTemplate/migrate?templateId=%d&templateStorageId=%d";
        /**
         * 根据条件查询所有符合条件的虚拟机
         */
        String DOMAIN_QUERY_FILTER = DOMAIN + "/vmList?hpId=%s";
        /**
         * 修改虚拟机配置
         */
        String DOMAIN_MODIFY = DOMAIN + "/modify";
        /**
         * 修改虚拟机类型
         */
        String DOMAIN_MODIFY_TYPE = DOMAIN + "/modify/vmType";
        /**
         * 根据虚拟机uuid查询指定虚拟机的网络信息。
         */
        String DOMAIN_QUERY_NETWORK_BY_UUID = DOMAIN + "/network/%s";
        /**
         * 克隆指定的虚拟机
         */
        String DOMAIN_CLONE = DOMAIN + "/clone";
        /**
         * 克隆指定的虚拟机，该接口为课程备份专门定制
         **/
        String DOMAIN_CLONE_COURSE_BACKUP = DOMAIN + "/courses/backup/shareMode";
        /**
         * 修改指定ID的虚拟机名称
         */
        String DOMAIN_RENAME = DOMAIN + "/rename/%d/%s";
        /**
         * 批量克隆虚拟机
         */
        String DOMAIN_BATCH_CLONE = DOMAIN + "/batchclone";
        /**
         * 批量删除虚拟机
         */
        String DOMAIN_BATCH_DELETE = DOMAIN + "/batchdel";
        /**
         * 合并指定虚拟机镜像文件
         */
        String DOMAIN_MERGE_VOL = DOMAIN + "/mergeDomainVol/%d";
        /**
         * 为指定虚拟机下发Acl策略
         */
        String DOMAIN_ISSUE_ACL = DOMAIN + "/issueAcl";

        /**
         * 查询虚拟机是否可修改
         **/
        String API_IS_ABLE_DOMAIN_MODIFY = "/cas/casrs/vm/isAbleModify/%s";

        /**
         * 查询虚拟机性能监控数据
         **/
        String DOMAIN_MONITOR_INFO = "/cas/casrs/vm/%s/monitor";
        String DOMAIN_MONITOR_INFOS = "/cas/casrs/vm/%s/monitor";

        /**
         * 查询虚拟机详情
         **/
        String DOMAIN_DETAIL_QUERY = "/cas/casrs/vm/%s/detail/all";

        /**
         * 查询虚拟机cpu利用率
         **/
        String DOMAIN_CPU_TREND = "/cas/casrs/vm/%d/cpuTrend";

        /**
         * 查询虚拟机内存利用率
         **/
        String DOMAIN_MEM_TREND = "/cas/casrs/vm/%d/memTrend";

        /**
         * 删除虚拟机到回收站
         **/
        String DLETE_INTO_RECYCLER = DOMAIN + "/delete/uuid/%s";

        /**
         * 查询虚拟机磁盘信息
         */
        String DOMAIN_DISK_INFO = DOMAIN + "/%s/config";
        /**
         * 查询mac地址
         */
        String DOMAIN_MACS = DOMAIN + "/macs";

        String DOMAIN_DESK_QUERY_ALL = DOMAIN + "/disk/all";

        String QUERY_DOMAIN_BASIC_INFO_LIST = DOMAIN + "/list/basicInfo";

        String QUERY_DOMAIN_BASIC_INFO_CONDITION = DOMAIN + "/uuids/list";

        String BATCH_DELETE = "/cas/domain/batchdelete";

        String QUERY_TEMPLATE = "/cas/domain/%d";

        /**
         * 查询虚拟机详情
         */
        String QUERY_DOMAIN_DETAIL_BY_DOMAIN_ID = DOMAIN + "/%s/summary";

        /**
         * 虚拟机cpu利用率
         */
        String QUERY_DOMAIN_CPU_USAGE = "/cas/casrs/vm/%s/cpuTrend";
        /**
         * 虚拟机内存利用率
         */
        String QUERY_DOMAIN_MEM_USAGE = "/cas/casrs/vm/%s/memTrend";

        /**
         * 虚拟机磁盘请求
         */
        String QUERY_DOMAIN_IO_IOPS = "/cas/casrs/vm/%s/IOPSTrend";
        /**
         * 虚拟机网络吞吐量
         */
        String QUERY_DOMAIN_NET_IOPS = "/cas/casrs/vm/%s/netRwTrend";
        String QUERY_DOMAIN_DISK_LATENCY = "/cas/casrs/vm/%s/diskLatencyTrend";
        String QUERY_DOMAIN_DISK_USAGE = "/cas/casrs/vm/%s/diskRateTrend";
        String QUERY_DOMAIN_PARTITION_USAGE = "/cas/casrs/vm/%s/partitionList";


        /**
         * 查询虚拟机详情
         */
        String QUERY_DOMAIN_DETAIL_BY_DOMAIN_ID_NEW = DOMAIN + "/%s/summary/new";
        /**
         * 连接数
         */
        String QUERY_DOMAIN_LINK_NUM = DOMAIN + "/%s/linkTrend";
        /**
         * cpu使用情况
         */
        String QUERY_DOMAIN_CPU_USE = DOMAIN + "/%s/domainUseCpuTrend";
        String QUERY_DOMAIN_DISK_THROUGHPUT = DOMAIN + "/%s/ioRwTrend";
        /**
         * 查询指定虚拟机的状态
         */
        String QUERY_DOMAIN_STATUS = DOMAIN + "/%s/status";
    }

    /**
     * 存储
     */
    interface Storage {
        String SHARE_STORAGE_START = "/cas/storage/cluster/pool/start";
        String SHARE_STORAGE_STOP = "/cas/storage/cluster/pool/stop";
        String SHARE_STORAGE_REFRESH = "/cas/storage/cluster/pool/refresh";

        /*新增存储卷*/
        String STORAGE_VOLUME_ADD_URL = CAS_NAME + "/storage/volume";

        /**
         * 删除存储卷
         */
        String STORAGE_VOLUMEDELETE_URL = CAS_NAME + "/storage/volume/delete";

        /**
         * 查询CAS系统中主机下的存储池列表
         */
        String STORAGE_POOL_IN_HOST_URL = CAS_NAME + "/storage/pool?hostId=%s";

        /**
         * 查询存储池详细信息
         */
        String HOST_STOREPOOLINFO_URL = CAS_NAME + "/storage/info";

        /**
         * 查询存储池详细信息
         */
        String HOST_STORE_POOL_BASE_URL = CAS_NAME + "/storage/pool";

        String STORAGE_COPY_ToCvk = CAS_NAME + "/storage/copyToCvk";

        /**
         * 指定主机增加存储池
         */
        String STORAGE_POOL_ADD_URL = CAS_NAME + "/storage/add";

        /**
         * 启动存储池
         */
        String STORAGE_POOL_START_URL = CAS_NAME + "/storage/start";

        /**
         * 在指定主机下刷新指定存储池
         */
        String STORAGE_POOL_REFRESH_URL = CAS_NAME + "/storage/refresh";

        /**
         * 查询CAS系统中主机下指定存储池下的存储卷列表。
         */
        String STORAGE_VOLUME_QUERY_URL = CAS_NAME + "/storage/volume";

        /**
         * 删除存储池
         */
        String STORAGE_POOL_DELETE = CAS_NAME + "/storage/delete";

        /**
         * 暂停存储池
         */
        String STORAGE_POOL_STOP = CAS_NAME + "/storage/stop";

        String STORAGE_POOL_QUERY = "/cas/storage/queryStoragePoolList?id=%s";

        /**
         * 查询存储池列表信息
         */
        String HOST_STORAGE_POOL_URL = CAS_NAME + "/storage/host/pool";

        /**
         * 查询指定主机下的存储卷信息
         */
        String STORAGE_HOST_VOLUME_ALL_BY_HOST_ID = CAS_NAME + "/storage/host/volume/all?hostId=%s";

        /**
         * 查询指定集群中的共享文件系统信息
         */
        String STORAGE_FS_BY_CLUSTER_ID = CAS_NAME + "/storage/fs/query?id=%s";

        /**
         * 查询指定集群中使用共享存储的主机信息
         */
        String STORAGE_FS_HOST_BY_CLUSTER_ID_AND_FS_ID = CAS_NAME + "/storage/fs/queryHost?id=%s&fsId=%s";
    }

    /**
     * 虚拟机模板
     */
    interface VmTemplate {
        //查询虚拟机模板信息
        String VM_TEMPLATE_BASIC_INFO = CAS_NAME + "/vmTemplate/vmTemplateInfo?id=%s";

        //查询虚拟机模板存储信息
        String VMTEMPLATE_STORAGE_URL = CAS_NAME + "/vmTemplate/storage?vmId=%s";

        //查询虚拟机模板网络信息
        String VMTEMPLATE_NETWORK_URL = CAS_NAME + "/vmTemplate/network?vmId=%s";

        //查询虚拟机全部模版列表
        String VMTEMPLATE_QUERY_ALL = CAS_NAME + "/vmTemplate/vmTemplateList?offset=0&limit=" + (Integer.MAX_VALUE - 1);

        //保存虚拟机模板
        String VMTEMPLATE_SAVE_TEMPLATE = CAS_NAME + "/vmTemplate/fromFile";
    }

    /*****************云学堂使用**************/
    /**
     * ACL策略
     */
    interface Acl {
        //增加ACL策略。
        String ACL_ADD_URL = CAS_NAME + "/acl";

        //查询指定的ACL策略。
        String ACL_QUERY_BY_ID_URL = CAS_NAME + "/acl/%d";

        //删除网络策略模板。
        String ACL_DELETE_BY_ID_URL = CAS_NAME + "/acl/%d";

        //修改ACL策略。
        String ACL_MODIFY_URL = CAS_NAME + "/acl";

        //根据条件查询ACL 策略列表。
        String ACL_QUERY_BY_CONDITION = CAS_NAME + "/acl";
    }

    /**
     * 网络策略模板
     */
    interface PortProfile {
        //增加ACL策略。
        String Profile_ADD_URL = CAS_NAME + "/profile";

        //查询指定的ACL策略。
        String Profile_QUERY_BY_ID_URL = CAS_NAME + "/profile/%d";

        //删除网络策略模板。
        String Profile_DELETE_BY_ID_URL = CAS_NAME + "/profile/%d/%s";

        //修改ACL策略。
        String Profile_MODIFY_URL = CAS_NAME + "/profile";

        String PROFILE_QUERY = "/cas/network/portprofile";
    }


    /**
     * 资源池
     */
    interface ResPool {

        /**
         * 查询资源池下资源使用历史列表
         */
        String QUERY_RESPOOL_HISTORY = CAS_NAME + "/resPool/history?resPoolId=";

        /**
         * 查询资源池下虚拟机列表
         */
        String QUERY_RESPOOL_VM = CAS_NAME + "/resPool/vm?resPoolId=";

        /**
         * 查询业务模板列表
         */
        String QUERY_BUSINESSTEM = CAS_NAME + "/resPool/queryBusinessTem";

        /**
         * 增加业务模板
         */
        String ADD_BUSINESSTEM = CAS_NAME + "/resPool/temp/add";

        /**
         * 虚拟机增加gpu/vgpu
         */
        String VM_ADDGPU = CAS_NAME + "/resPool/addGpu";

        /**
         * 卸载虚拟机的GPU设备
         */
        String VM_DEL_GPU = CAS_NAME + "/vm/delDevice";

        /**
         * 虚拟机删除gpu/vgpu
         */
        String VM_DELGPU = CAS_NAME + "/vm/delDevice";

        /**
         * 查询资源池详细信息
         */
        String QUERY_RESPOOL = CAS_NAME + "/resPool/";

        /**
         * 根据主机ID查询主机上的物理GPU列表。
         */
        String QUERY_HOST_GPU_LIST = CAS_NAME + "/resPool/hostGpuList?hostId=%d";

        /**
         * 根据集群ID查询CAS系统中的资源池列表。
         */
        String QUERY_ALL_CLUSTER_RES_POOL = CAS_NAME + "/resPool/queryResPool";

        /**
         * 查询集群下空闲vGPU资源列表。
         */
        String QUERY_VGPU_LIST_IN_RES_POOL = CAS_NAME + "/resPool/queryVgpuList?clusterId=%d&id=%d";

        /**
         * 根据集群ID查询CAS系统中的资源池列表。
         */
        String QUERY_CLUSTER_RESPOOL = CAS_NAME + "/resPool/queryResPool?clusterId=%d";

        /**
         * 修改资源池。
         */
        String MODIFY_RESPOOL = CAS_NAME + "/resPool/modify";

        /**
         * 查询集群资源池列表
         */
        String QEURY_RESPOOLS = CAS_NAME + "/resPool/queryResPool?clusterId=";

        /**
         * 查询vGPU基本信息
         */
        String QUERY_VGPU_BASIC_INFO = CAS_NAME + "/resPool/vgpu/list?resPoolId=%d";

        /**
         * 修改资源池虚拟机。
         */
        String MODIFY_RESPOOL_VM = CAS_NAME + "/resPool/modify/vms?type=%s";
    }

    /**
     * license
     */
    interface License {
        /**
         * 通知casserver license信息
         */
        String NOTIFY_CAS_LICENSE_INFO = CAS_NAME + "/cvm/vdi/runInit";
    }

    /**
     * 查询CVM架构
     */
    interface Infrastructure {
        /**
         * 获取cvm底层架构信息
         */
        String QUERY_CVM_INFRASTRUCTURE_INFO = CAS_NAME + "/system/cvm";

        /**
         * 获取cvk底层架构信息
         */
        String QUERY_CVK_INFRASTRUCTURE_INFO = CAS_NAME + "/system/host/%s";

    }

    interface Warn {
        String QUERY_WARN = CAS_NAME + "/warnManage/warnManageList?offset=%s&limit=%s&eventTime_from=%s&eventTime_to=%s";
        String WARN_REAL_TIME_ALARMS = CAS_NAME + "/alarm/realTimeAlarms?eventTime_from=%s&eventTime_to=%s&state=%s";
    }

    interface Image {
        String QUERY_VDI_IMAGE_DETAILS = "cas/domain/%d";
        String COMPRESS_IMAGE = "cas/template/%s/compress";
        String DOWNLOAD_IMAGE = "/cas/download/template?filePath=%s";
    }

    interface DesktopPool {
        String START_DESKTOPPOOL_DOMAINS = "cas/domain/batch/start";
        String RESTART_DESKTOPPOOL_DOMAINS = "cas/domain/batch/restart";
        String SHUTDOWN_DESKTOPPOOL_DOMAINS = "cas/domain/batch/shutdown";
    }

    interface Dashboard {
        String QUERY_HOST = "cas/dashboard/host";
        String QUERY_VM = "cas/dashboard/vm";
        String QUERY_CPU = "cas/dashboard/hostCpu";
        String QUERY_MEM = "cas/dashboard/hostMem";
        String QUERY_VM_CPU = "cas/dashboard/vmCpu";
        String QUERY_VM_MEM = "cas/dashboard/vmMem";
        String QUERY_VM_DISK = CAS_NAME + "/reports/vm/disk/utilizationTop?topNum=%s";
        String HOST_DISK_UTILIZATION_TOPN = CAS_NAME + "/reports/cluster/%s/hostTopNDiskUtilization";
    }

    interface Operator {
        /**
         * 查询所有的操作员
         */
        String QUERY_OPERATOR = "cas/casrs/operator/operatorInfoList";
    }

    interface Log {
        /**
         * 日志收集(controller接口)
         */
        String GATHER_LOG = "/cas/operationlog/gatherLog";

        /**
         * 日志收集下载(controller接口)
         * */
        String DOWNLOAD_LOGFILE = "/cas/download/logfile";

        /**
         * 虚拟机操作日志(controller接口)
         * */
        String OPERATION_LOGS_VM = "/cas/operationlog/queryOperationLogByCondition?category=%d&limit=%d&offset=%d";
    }

    interface SshAuth{
        /**
         * 查询是否开启远程协助
         * /cas/casrs/parameter/{type}
         */
        String FIND_SSH_CONF="/cas/casrs/parameter/ssh_conf";
        /**
         * 用户登录接口—查询是否有对应参数修改权限
         * /cas/spring_check
         * /cas/spring_check?t=1662348880391&encrypt=true&lang=cn&name=WpIxDAPVsRM%3D&password=HXTtHlTzAqNJ9fWHAhnwDg%3D%3D&isForce=
         */
        String CAS_LOGIN ="/cas/spring_check?t=%s&encrypt=false&lang=cn&name=%s&password=%s&isForce=";
        /**
         * 修改系统基本参数
         * 	/cas/casrs/parameter/add
         * 	/cas/casrs/parameter/add?type=ssh_conf
         */
        String MODIFY_CAS_SSH_AUTH ="/cas/casrs/parameter/modifyEnableSSH";
//        String MODIFY_CAS_SSH_AUTH ="/cas/casrs/parameter/modifyEnableSSH";
    }

}
