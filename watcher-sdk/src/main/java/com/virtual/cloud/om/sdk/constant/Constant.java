package com.virtual.cloud.om.sdk.constant;

import java.io.File;

/**
 * @Author: w22798
 * @Date: 2022/4/23 16:25
 */
public interface Constant {

    String RESOURCE_WORKSPACE = "workspace";
    String RESOURCE_UIS = "uis";
    String RESOURCE_CAS = "cas";
    String RESOURCE_ONESTOR = "onestor";
    String  TARGET="&target=";

    //操作类型
    String OPERATE_STARTUP = "startup";
    String OPERATE_SHUTDOWN = "shutdown";
    String OPERATE_RESTART = "restart";

    //操作日志队列
    String KAFKA_TOPIC_LOG = "topic-log-receiver";
    //filebeat日志采集队列
    String KAFKA_TOPIC_FILEBEAT = "topic-filebeat-receiver";

    String username = "admin";
    String password = "Cloud@1234";

    String TOKEN_NAME = "OAD_TOKEN";
    String ACCESS_PATH = "/";
    Integer COOKIE_EXPIRED = 0;

    interface Tags {
        String HOST_IDS = "hostIds";
        String CLUSTER_IDS = "clusterIds";
        String DOMAIN_IDS = "domainIds";

        String CLUSTER_IDS_ALL = "clusterIds=-1";
        String HOST_IDS_ALL = "hostIds=-2";
        String DOMAIN_IDS_ALL = "domainIds=-3";
//        String FIND_ALL = "-1";

        String CULSTER_IDS_ALL_VALUE ="-1";
        String HOST_IDS_ALL_VALUE ="-2";
        String DOMAIN_IDS_ALL_VALUE ="-3";

        String HOST_ID = "hostId";
        String CLUSTER_ID = "clusterId";
        String DOMAIN_ID = "domainId";
    }

    interface Deploy {
        String STATUS_UNKNOWN = "unknown";
        String STATUS_STARTUP = OPERATE_STARTUP;
        String STATUS_SHUTDOWN = OPERATE_SHUTDOWN;
        String SERVICE_NAME_KAFKA = "kafka";
        String SERVICE_NAME_ZOOKEEPER = "zookeeper";
        String SERVICE_NAME_MONGODB = "mongodb";
        String SERVICE_NAME_AGENT = "agent";
        String SERVICE_NAME_NGINX = "nginx";
        String SERVICE_NAME_KEEPALIVED = "keepalived";
        // 采集端各个节点的网络配置信息记录文件
        String NETWORKS_CONFIG_FILE_PATH = "/etc/watcher-nodes-network-config";
        String IFCFG_FILE_CONTENT_DEMO = "TYPE=Ethernet\n" +
                "PROXY_METHOD=none\n" +
                "BROWSER_ONLY=no\n" +
                "DEFROUTE=yes\n" +
                "IPV4_FAILURE_FATAL=no\n" +
                "IPV6INIT=yes\n" +
                "IPV6_AUTOCONF=yes\n" +
                "IPV6_DEFROUTE=yes\n" +
                "IPV6_FAILURE_FATAL=no\n" +
                "IPV6_ADDR_GEN_MODE=stable-privacy\n" +
                "ONBOOT=yes\n";
    }

    interface DataCenter {
        Integer SUCCESS = 0;
        Integer PARTIAL_SUCCESS = 1;
        Integer FAIL = 2;
        Integer STEP_NETWORK = 0;
        Integer STEP_DEPLOY_AUTH_FAIL = 1;
        Integer STEP_DEPLOY = 2;
        Integer STEP_AUTH_SUCCESS = 3;
        Integer MINLENGTH = 8;
        Integer PWDLIFETIME = 0;
        Integer PWDCOMPLEX = 4;

        Integer SIMPLE_PASSWORD = 0;

        Integer UNCLOUD = 0;
        Integer CLOUD = 1;
    }

    interface Websocket {
        String PINT = "ping";
        String STRATEGY_REST = "metric";
        String STRATEGY_LOG = "log";
        String STRATEGY_LOG_REAL_TINE = "log-real-time";
        String MANDATORY = "mandatory";
        String STRATEGY_RESOURCES = "resources";
        String TEST_SSH_CONNECT = "testSshConnect";
        String SSH_CONNECT = "sshConnect";
        String IINSPECT_ISSUE = "inspect";
        String WARN_STRATEGY_REST = "warn";
        String OPERATE = "operate";
        String OPERATE_REFRESH_STATUS = "refreshStatus";
        String WEBSOCKET_STATE = "kick";
        String AGENT_UPGRADE_ONLINE = "upgrade";
        String SSH_REMOTE_AUTH = "sshRemote";
        String WATCHER_ROUTE_CHECK = "watcherRouteCheck";
        String WATCHER_ROUTE_ADD_CHECK = "watcherRouteAddCheck";
        String WATCHER_ROUTE_EDIT_CHECK = "watcherRouteModifyCheck";
        String WATCHER_ROUTE_QUERY = "watcherRouteQuery";
        String WATCHER_ROUTE_ADD = "watcherRouteAdd";
        String WATCHER_ROUTE_EDIT = "watcherRouteModify";
        String WATCHER_ROUTE_DELETE = "watcherRouteDelete";

        /**
         * websocket 心跳锁
         */
        String WEBSOCKET_LOCK = "websocket";

        /**
         * 运维中心 token过期的剩余有效时长
         */
        Long surplusExpireTimeMs = 5 * 60 * 1000L;

        int not_lose_connection = -1;
        /**
         * 断开连接，已被抢占
         */
        int lose_connection = 0;
        /**
         * 抢占成功
         */
        int connect_success = 1;
        /**
         * 抢占失败
         */
        int connect_fail = 2;

        /**
         * 租户失效
         */
        int tenant_error = 3;
        /**
         * 数据中心主动断开
         * */
        int dis_connection = 4;

        /**
         * 不开启
         */
        int SSH_REMOTE_TYPE_CLOSE =-1;

        int SSH_REMOTE_OPEN = 1;
        int SSH_REMOTE_CLOSE = 0;

        /**
         * oam
         */
        int SSH_OAM_REMOTE_TYPE_ONCE =1;
        int SSH_OAM_REMOTE_TYPE_ONE_DAY =2;
        int SSH_OAM_REMOTE_TYPE_THREE_DAY =3;
        int SSH_OAM_REMOTE_TYPE_SEVEN_DAY =4;
        /**
         * watcher
         */
        int SSH_WATCHER_REMOTE_TYPE_ONEDAY =1;
        int SSH_WATCHER_REMOTE_TYPE_THREE_DAY =2;
        int SSH_WATCHER_REMOTE_TYPE_SEVEN_DAY =3;

        Long ONE_DAY_MILL=24*60*60*1000L;

        Long THREE_DAY = 3* ONE_DAY_MILL;
        Long SEVEN_DAY = 7* ONE_DAY_MILL;
        Integer SSHREMOTERES_SUCCESS =1;
        Integer SSHREMOTERES_FAIL = 2;

    }

    interface LockKey {
        String KEY_REALTIME_LOG = "key_realtime_log_%s";
        String STATIC_DATA_REPORT = "STATIC_DATA_REPORT_%s";
    }

    interface Parameter {
        /**
         * 系统基本参数type
         */
        String SYS_CONF = "sys_conf";

        /**
         * 虚IP
         */
        String NAME_VIP = "vip";
        String NAME_CLUSTER_STEP = "cluster_step";
        String NAME_CLUSTER_STATUS = "cluster_status";
        String NAME_CLUSTER_RESULT = "cluster_result";
        String NAME_CLUSTER_RESULT_SUCCESS = "success";
        String NAME_CLUSTER_RESULT_FAILURE = "failure";

        /**
         * 日志收集ID记录
         */
        String Log_Collect_last_Id_Tag = "%s-logCollectLastId";
        String Log_Collect_last_Id_Name = "%s-logCollectLastId";
    }

    interface RealtimeLog {
        String TARGET_TYPE_SERVER = "server";
        String TARGET_TYPE_HOST = "host";
        String TARGET_TYPE_VM = "vm";
        String TARGET_TYPE_CLIENT = "client";
        String ES_INDEX_NAME_LOG="serverlog";
        String ES_INDEX_NAME_LOG_ALIAS="serverlog.aliases";

    }

    interface Version {
        StringBuilder VERSION_PATH = new StringBuilder("/etc");
        String WORKSPACE_VERSION = "workspace-server.version";
        String UIS_VERSION = "uis-version";
        String CAS_VERSION = "cas_cvk-version";
        String ONESTOR_VERSION = "onestor_external_version";
        String WORKSPACE_VERSION_PATH = VERSION_PATH.append(File.separator).append(WORKSPACE_VERSION).toString();
        String UIS_VERSION_PATH = VERSION_PATH.append(File.separator).append(UIS_VERSION).toString();
        String CAS_VERSION_PATH = VERSION_PATH.append(File.separator).append(CAS_VERSION).toString();
        String ONESTOR_VERSION_PATH = VERSION_PATH.append(File.separator).append(ONESTOR_VERSION).toString();
        String ONESTOR_VERSION_NUM = "3";
    }
    interface AgentUp{
        Integer UPGRADE_STATUS_SUCCESS = 1;
        Integer UPGRADE_STATUS_FAIL = 2;
    }

    interface Cmd{
        Integer CMD_FAIL_WAIT =3;
    }

    interface Log {
        String LINE = "line";
        String CSV_SEPARATOR = ",";
        Integer CSV_TYPE = 0;
        Integer JSON_TYPE = 1;
        Integer NO_COMPRESS_TYPE = 0;
        Integer GZIP_TYPE = 1;
        Integer ZIP_TYPE = 2;

        String SERVER = "server";
        String HOST = "host";
        String VM = "vm";
        String DOMAIN = "domain";
        String TERMINAL = "terminal";
        String WORKSPACE = "workspace";
        String UIS = "uis";
        String CAS = "cas";


        String WORKSPACE_LOG_1 = "/var/log/vdi/controller/controller.log";
        String WORKSPACE_LOG_2 = "/var/log/vdi/workspace-server/workspace.log";
        String WORKSPACE_LOG_3 = "/var/log/vdi/controller/grpc.log";
        String WORKSPACE_LOG_4 = "/var/log/vdi/controller/grpc-client.log";
        String HOST_UIS_LOG_1 = "/var/log/uis-core/uis.log";

        String CAS_LOG_1 = "/var/log/tomcat8/cas.log";
        String CAS_LOG_2 = "/var/lib/casserver/logs/casserver.log";
        String CAS_LOG_3 = "/var/log/tomcat8/catalina.out";
        String CAS_LOG_4 = "/var/log/libvirt/libvirtd.log";
        String CAS_LOG_5 = "/var/log/fsm/fsm_core.log";
        String CAS_LOG_6 = "/var/log/messages";
        String CAS_LOG_7 = "/var/log/libvirt/virtagent.log";
        String CAS_LOG_8 = "/var/log/openvswitch/ovs-vswitchd.log";
        String CAS_LOG_9 = "/var/log/caslog/cas_na_ctl.log";
        String CAS_LOG_10 = "/var/log/caslog/cas_ovs_shell.log";
        String CAS_LOG_11 = "/var/log/libvirt/qemu/*.log";

        String UIS_LOG_1 = "/var/log/uis-core/uis.log";
        String VM_LOG_1 = "";
        String TERMINAL_LOG_1 = "";

        String EXPORT_LOG_TEMP_PATH = "/data/oad-center/log/export/tmp/";
        String LOG_TEMP_PATH = "/data/oad-center/log/upload/tmp/";
        int EXPIRE_DAY = 7;
        String FILE_UPLOAD_TMP_PATH = "/vms/tmp";
        String FILE_UPLOAD_PATH = "/data/oad-center/log/upload";
        String EXPORT_LOG_PATH = "/data/oad-center/log/export";
        String INDEX = "log";
        String TIME = "time";
        int QUERY_MAX_LENGTH = 20000;
        int ALL = 0;
        int ERROR = 1;
        int WARN = 2;
        int INFO = 3;
        int DEBUG = 4;
        int TRACE = 5;

        String ERROR_STRING = "ERROR";
        String WARN_STRING = "WARN";
        String INFO_STRING = "INFO";
        String DEBUG_STRING = "DEBUG";
        String TRACE_STRING = "TRACE";

        int SUCCESS = 0;
        int PARTIAL_SUCCESS = 1;
        int FAIL = 2;
        int COLLECTING = 3;
    }
}
