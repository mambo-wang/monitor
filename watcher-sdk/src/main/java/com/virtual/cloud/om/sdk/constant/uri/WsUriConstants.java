package com.virtual.cloud.om.sdk.constant.uri;

/**
 * Created by x19765 on 2020/11/17.
 */
public interface WsUriConstants {
    // 主机日志收集
    String GATHER_LOG = "/vdi/operation-log/gatherLog";
    // 主机日志收集结果
    String GATHER_LOG_RESULT = "/vdi/operation-log/gratherLogResult";
    // 主机日志收集下载
    String GATHER_LOG_DOWNLOAD = "/vdi/operation-log/downloadLogs/%s";

    // 桌面(虚拟机)日志收集
    String VM_LOG_COLLECT = "/vdi/desktoppools/vmLogs/collect?title=%s&vmUuid=%s";
    // 查看桌面(虚拟机)日志收集结果列表
    String VM_LOGS = "/vdi/desktoppools/vmLogs";
    // 下载桌面(虚拟机)日志
    String VM_LOG_DOWNLOAD = "/vdi/desktoppools/vmLogs/%s/download/";

    // 终端日志收集
    String TERMINAL_LOG_COLLECT = "/vdi/devices/terminal-logs/collect?deviceIds=%s";
    // 终端日志收集结果列表
    String TERMINAL_LOGS = "/vdi/devices/terminal-logs?downloadable=true";
    // 下载终端日志
    String TERMINAL_LOG_DOWNLOAD = "/vdi/devices/terminal-logs/%s/download/";
    // 终端开机
    String TERMINAL_WAKE = "/vdi/devices/wakeUp";
    // 终端关机
    String TERMINAL_STOP = "/vdi/devices/shutDown";
    // 终端重启
    String TERMINAL_RESTART = "/vdi/devices/restart";

    String QUERY_TERMINAL_BASIC_REST = "/vdi/rest/oad/report/terminalBasic";
    String QUERY_TERMINAL_BASIC = "/vdi/devices";

    String QUERY_RESOURCE_VERSION = "/vdi/about/releaseVersion";

    interface Test {
        //连接测试、认证鉴权
        String TEST_CONNECTION = "/vdi/rest/center/testConnection";
    }

    interface DesktopPool {
        // ===== oad 新增接口 =====
        // 桌面池基本信息

        // ===== token接口 =====
        // 查询桌面池中虚拟机状态统计
        String QUERY_VMS_STAT= "/vdi/desktoppools/%s/vmstat";

        //查询桌面池列表
        String QUERY_DESKTOPPOOL_LIST_REST_CENTER = "/vdi/rest/center/desktoppools?computerType=%s";
        String QUERY_ClOUD_DESKTOPPOOL_LIST = "/vdi/rest/center/cloud/desktoppools?computerType=%s";
        String RESTART_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/desktoppools/%d/restart";
        String SHUTDOWN_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/desktoppools/%d/shutdown";
        String START_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/desktoppools/%d/start";
        // 查询某个桌面池
        String QUERY_DESKTOPPOOL_BY_ID = "/vdi/rest/center/pools/desktoppools/%s";
        //查询桌面池下虚拟机
        String QUERY_DOMAINS_BY_POOL_ID = "/vdi/rest/center/desktoppools/%d";
        String QUERY_CLOUD_DOMAINS_BY_POOL_ID = "/vdi/rest/center/cloud/desktoppools/%d";
        //桌面池下的虚拟机关机、开机、重启
        String SHUTDOWN_CLOUD_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/cloud/desktoppools/vms/stop";
        String START_CLOUD_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/cloud/desktoppools/vms/start";
        String REBOOT_CLOUD_DESKTOPPOOL_DOMAINS = "/vdi/rest/center/cloud/desktoppools/vms/reboot";
        // 桌面池列表
        String QUERY_DESKTOPPOOL_LIST_REST = "/vdi/rest/workspace/desktoppools";
        //查询桌面池详情
        String QUERY_DESKTOPPOOL_BYID = "/vdi/rest/workspace/desktoppools/%s";
        //查询桌面池详情
        String QUERY_DELETE_DESKTOPPOOL_BYID = "/vdi/rest/center/pools/desktoppools/%d";
        //添加、修改桌面池
        String EDIT_DESKTOPPOOL = "/vdi/rest/center/pools/desktoppools";
        //部署虚拟机
        String DEPLOY_VMS_IN_DESKTOPPOOL = "/vdi/rest/center/pools/desktoppools/%d/batchdeployvms";
        //添加桌面池预授权
        String ADD_AUTH_POOL = "/vdi/rest/center/pools/desktoppools/%d/saveauth";
        //查询桌面池分组
        String QUERY_DESKTOPPOOLGROUP_BYID = "/vdi/rest/center/pools/desktoppoolgroups/%d";
        //添加、修改桌面池分组
        String EDIT_DESKTOPPOOLGROUP = "/vdi/rest/center/pools/desktoppoolgroups";
        //查询桌面池历史镜像
        String QUERY_HISTORY_DESKTOPPOOLS = "/vdi/rest/workspace/desktoppools/getHistoryImageList";
        //修改桌面池镜像
        String MODIFY_DESKTOPPOOLS = "/vdi/rest/workspace/desktoppools/modify/template";
        String MODIFY_CLOUD_DESKTOPPOOLS = "/vdi/rest/workspace/cloud/desktoppools/modify/template/%d";
        //查询所有桌面镜像
        String QUERY_IMAGE_LIST = "/vdi/rest/workspace/desktoppools/template";
        String QUERY_IMAGE_BY_ID = "/vdi/rest/workspace/desktoppools/template?templateId=%s";
        //修改资源使用阈值
        String EDIT_RESOURCE_LIMITED = "/vdi/rest/center/pools/resourceLimited";
        //从某个桌面池中移除虚拟机
        String REMOVE_DESKTOPPOOLS_VMS = "/vdi/rest/center/pools/desktoppools/%d/vms/remove";
        //查询桌面池中的虚拟机
        String QUERY_DESKTOPPOOLS_VMS_REST = "/vdi/rest/center/pools/desktoppools/%d/vms";
        String QUERY_DESKTOPPOOLS_INFO_BY_ID = "/vdi/desktoppools/%s";
        String QUERY_DESKTOPPOOLS_LIST = "/vdi/desktoppools";
        String QUERY_DESKTOPPOOLS_VMS = "/vdi/desktoppools/%s/vms";
        String QUERY_DESKTOPPOOLS_TERMINALS = "/vdi/desktoppools/%s/terminals";
        //释放虚拟机（删除时调用）
        String RELEASE_DESKTOPPOOLS_VMS = "/vdi/rest/center/pools/desktoppools/%d/vms/release";
    }


    interface Image {
        String QUERY_TEMPLATE_INFO = "/vdi/template?templateId=%s";
        //查询镜像详情
        String QUERY_OTHER_IMAGE_DETAILS = "/vdi/rest/center/vmTemplate/imagefile/%d";
        //查询所有桌面镜像
        String QUERY_IMAGE_LIST = "/vdi/rest/center/template";
        //查询所有桌面镜像 云上
        String CLOUD_QUERY_IMAGE_LIST = "/vdi/rest/center/cloud/template/image?isPublished=true";
        //下载镜像文件
        String DOWNLOAD_IMAGE = "/vdi/rest/center/vmTemplate/imagefile/%d/download";
        //完成文件上传
        String FINSH_IMAGE_UPLOAD = "/vdi/rest/center/vmTemplate/fishUpload";
        //上传完成后导入镜像
        String IMPORT_VDI_IMAGE = "/vdi/rest/center/import?templateName=%s&templateStoragePath=%s";
        //导入IDV、VOI镜像
        String IMPORT_IDV_VOI_IMAGE = "/vdi/rest/center/vmTemplate/imagefile";
        //根据名称查询镜像
        String QUERY_IMAGE_BYNAME = "/vdi/rest/center/pools/image?name=%s";
        //删除镜像
        String DELETE_IMAGE = "/vdi/rest/center/pools/images/%d";
    }

    interface CourseImage {
        String QUERY_ALL_COURSE_IMAGE = "/vdi/rest/learning-space/data/course/queryAllCourse";
        String QUERY_COURSE_IMAGE_FOR_CLASSROOM = "/vdi/rest/learning-space/data/course/courseImages";
        String QUERY_COURSE_IMAGE = "/vdi/rest/learning-space/data/course/%d";
        String PUBLISH_COURSE_IMAGE = "/vdi/rest/center/course-images/publish";
        String CANCEL_PUBLISH_COURSE_IMAGE = "/vdi/rest/center/course-images/cancelPublish";
        String DOWNLOAD_COURSE_IMAGE = "/vdi/rest/center/course-images/download/%d";
        String DOWNLOAD_VOI_COURSE_IMAGE = "/vdi/vmTemplate/imagefile/%d/download";
        String FINISH_COURSE_IMAGE_UPLOAD = "/vdi/rest/center/course-images/import";
        String QUERY_COURSE_NAME = "/vdi/rest/learning-space/courseName/%d";
    }

    interface Host {
        String QUERY_CLUSTER_HOST = "/vdi/rest/center/cluster/host";
        String QUERY_VGPU_CONFIG = "/vdi/rest/center/vGPUConfigs/host/%d";
        //查询某主机的存储池
        String QUERY_STORAGE_POOL = "/vdi/desktoppools/storagePools?hostId=%s";
    }
    interface Sync {
        //登录接口
        String WS_SPACE_CONSOLE_LOGIN = "/vdi/login/doLogin/center";
        String SYNC_BASIC_PARAMETERS_GET = "/systemConfig/sysConfig?type=sys_conf";
        String SYNC_BASIC_PARAMETERS_POST = "/systemConfig/sysConfig";
        String SYNC_GPU = "/systemConfig/gpusysConfig";

        String SYNC_BASIC_CAS="/cas/systemConfig/sysConfig?type=sys_conf";
        String SYNC_DESKTOP_CAS = "/cas/systemConfig/sysConfig?type=sys_conf";
        String SYNC_BASIC_UIS = "/uis/uis/systemConfig/sysConfig?type=sys_conf";
        String QUERY_VSWITCHS = "/vdi/rest/center/pools/vswitchs";
        String SYNC_ADVANCE_SETING_CVM = "/systemConfig/modifyReserveMemory";
    }

    interface Warn {
        //实时告警（1：主机资源告警  2：虚拟机资源告警、3：集群资源告警、4：故障告警、5：操作告警 6：其他异常告警7：安全告警   20：分布式存储资源告警、101：桌面告警、99：虚拟应用告警）
        String WARN_REAL_TIME_ALARMS = "/vdi/warnManage/realTimeAlarms?eventTime_from=%s&eventTime_to=%s&state=%s";

        //VIP桌面告警
        String WARN_VIP_DESK_ALARMS = "/vdi/vip/desk/alarm/list?startTime=%s&endTime=%s&status=%s";

        //终端告警
        String WARN_TERMINAL_ALARMS = "/vdi/device/alarm?offset=%s&limit=%s&status=%s";
    }

    interface ResourceUser {
        String QUERY_USER = "/vdi/users/page?userType=%d&offset=%d&limit=%d";
        String OPERATOR = "/vdi/operator?offset=%d&limit=%d";
    }

    interface Log{
        String OPERATION_LOGS_VM = "/cas/operationlog/queryOperationLogByCondition?limit=%d&offset=%d&category=%d";
    }
    interface SshAuth{
        /**
         * 1.	查询是否开启远程协助
         */
      String VDI_QUERY_SSH_AUTH="/vdi/systemConfig/sysConfig/querySysConfigEnableSSH";
        /**
         * 修改系统基本参数—是否开启远程协助
         */
      String VDI_MODIFY_SSH_AUTH="/vdi/systemConfig/sysConfig/modifyEnableSSH";
        /**
         * 3.	查询用户是否有修改参数权限
         */
      String VDI_QUERY_USER_SSH_AUTH="/vdi/systemConfig/sysConfig/queryIfHaveEditPermission/%s";

    }
}
