package com.virtual.cloud.om.sdk.constant.uri;

/**
 * onestor url 不同的业务逻辑建立子的interface
 *
 * @author zkf9688
 * @date 2022/07/22 10:30
 */
public interface OnestoreUriConstants {

    String ONSTOREBASE = "/api/v3";

    interface license {
        String license = ONSTOREBASE + "/allowany/plat/cloudos/ip";
    }

    interface auth {
        String AUTH = "/api/v1/auth/login";
    }

    interface Warn {
        String ONESTORE_ALARM_COUNT = ONSTOREBASE+ "/onestor/%s/plat/alarm/count";
        String ONESTORE_ALARM_REAL = ONSTOREBASE+ "/onestor/plat/alarm/real";

    }

    /**
     * 集群
     */
    interface Cluster {
        //集群信息
        String CLUSTER_STOR_BASIC = ONSTOREBASE + "/onestor/%s/plat/namequery";
        //当前管理节点下的子节点信息
        String CLUSTER_SERVER_INFO = ONSTOREBASE + "/onestor/plat/servers/ip?roles=%s";

        /**
         * 获取集群id
         */
        String ONESTOR_CLUSTER_ID = ONSTOREBASE + "/plat/cluster";
        /**
         * 集群基本信息
         * api/v3/onestor/<集群ID>/plat/namequery
         */
        String STOR_CLUSTER_BASIC = ONSTOREBASE + "/onestor/%s/plat/namequery";
        /**
         *
         */
        String STOR_CLUSTER_MONITOC= ONSTOREBASE + "/onestor/%s/plat/cluster/health";
        /**
         * 集群容量
         */
        String STOR_CLUSTER_CAPACITY_BASIC= ONSTOREBASE + "/onestor/%s/poolcapacity";
        /**
         * http://10.99.201.131/graphite/render?format=json&from=-5minute&target=big_cluster.pool.all.iops.rd
         */

        String STOR_CLUSTER_MONITOR= "/graphite/render?format=json&from=-5minute";

        //获取集群ID等基本信息
        String CLUSTER_INFO = ONSTOREBASE + "/plat/cluster";
        String CLUSTER_INFO_ALL ="/graphite/render?format=json&from=-60minute&target=big_cluster.pool.all.iops.rd&target=big_cluster.pool.all.iops.wr&target=big_cluster.recovery.ops&target=big_cluster.pool.all.iops.all&target=big_cluster.fs.ops.rd&target=big_cluster.fs.ops.wr&target=big_cluster.fs.ops.total&target=big_cluster.pool.all.bw.rd&target=big_cluster.pool.all.bw.wr&target=big_cluster.recovery.bw&target=big_cluster.pool.all.bw.all&target=big_cluster.fs.bw.rd&target=big_cluster.fs.bw.wr&target=big_cluster.fs.bw.total&target=big_cluster.pool.all.data.rd&target=big_cluster.pool.all.data.wr&target=big_cluster.pool.all.data.rc&target=big_cluster.fs.data.rd&target=big_cluster.fs.data.wr&target=big_cluster.space.total&target=big_cluster.space.used&target=big_cluster.cpu.ratio&target=big_cluster.mem.ratio&target=big_cluster.diskstat.lat.rd&target=big_cluster.diskstat.lat.wr&target=big_cluster.diskstat.util.avg&target=big_cluster.diskstat.util.max";
    }

    /**
     * 日志
     * */
    interface Log{
        //日志收集
        String ONESTOR_SYSTEM_LOG = ONSTOREBASE + "/onestor/plat/systemlog";
        //下载日志文件
        String DOWNLOAD_LOG_FILE = "/static/temp/%s";
    }

    /**
     * 主机
     * */
    interface Host{
        //主机角色信息
        String ROLE_INFO = ONSTOREBASE + "/onestor/%s/plat/servers/ip?roles=%s";
        //存储节点信息
        String STOR_INFO = ONSTOREBASE + "/onestor/%s/plat/server?ver=%s";
        //监控节点信息
        String MONITOR_INFO = ONSTOREBASE + "/onestor/%s/plat/mon";
        //NAS节点信息
        String NAS_INFO = ONSTOREBASE + "/onestor/%s/filestorage/nas/server?is_host_nas=true";
        //MDS节点信息
        String MDS_INFO = ONSTOREBASE + "/onestor/%s/filestorage/mds?is_host_mds=true";

        //根据类型查询主机(同时查多角色使用逗号分隔角色)
        String GET_HOSTS_BY_ROLES = ONSTOREBASE + "/onestor/plat/servers/ip?roles=%s";
        String HOST_GRAPHITE= "/graphite/render?format=json&from=-5minute";

    }

    /**
     * 节点池
     * */
    interface NodePool{
        //节点池基本信息
        String BASIC_INFO_NODE_POOL = ONSTOREBASE + "/onestor/%s/plat/nodepool";
    }

    /**
     * 硬盘池
     * */
    interface DiskPool{
        //硬盘池基本信息
        String BASIC_INFO_DISK_POOL = ONSTOREBASE + "/onestor/%s/plat/diskpool?nodepool_name=%s";
        String BASIC_INFO_DISK= ONSTOREBASE + "/onestor/%s/plat/disk/query?host_name=%s";
//        http://10.99.201.98/api/v3/onestor/92562e06-1e74-4d9a-9628-df2bb750d5af/plat/disk/query?host_name=pc-98

    }

    /**
     * 存储池
     * */
    interface Pool{
        //存储池基本信息
        String BASIC_INFO_POOL = ONSTOREBASE + "/onestor/%s/plat/pool/query";
    }

    /**
     * 监控数据
     * */
    interface Monitor{
        //IOPS
        String MONITOR_BY_TARGET = "/graphite/render?format=json&from=-1minute";
    }
}
