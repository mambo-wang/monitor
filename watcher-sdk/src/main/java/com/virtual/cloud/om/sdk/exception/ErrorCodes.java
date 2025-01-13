package com.virtual.cloud.om.sdk.exception;

import com.virtual.cloud.om.sdk.utils.StringManager;

import java.util.Objects;

/**
 * Created by w22798 on 2020/11/03
 */
public interface ErrorCodes {

    String ERROR_PACKAGE = "messages.ErrorCode";
    StringManager stringManager = StringManager.getManagerWithBundleName(ERROR_PACKAGE);
    String PREFIX = "errorCode.";
    //公共错误 1-100
    int DEFAULT_UNDEFINED_ERROR = 1;
    /**
     * 对象解析错误。
     */
    int OBJECT_PARSE_ERROR = 2;
    /**
     * 权限不足。
     */
    int NO_PERMISSION = 3;
    /**
     * 接口调用失败，HTTP请求出错。
     */
    int HTTP_RESPONSE_ERROR = 4;
    /**
     * 操作失败。
     */
    int OPERATE_FAIL = 5;
    /**
     * 未知错误。
     */
    int UNKNOWN_ERROR = 6;
    /**
     * 数据库访问错误。
     */
    int DATABASE_ERROR = 7;
    /**
     * 文件读写错误。
     */
    int FILE_ACCESS_ERROR = 8;
    /**
     * 参数为空。
     */
    int PARAMETER_IS_NULL = 9;
    /**
     * 参数值为空。
     */
    int PARAMETER_VALUE_IS_NULL = 10;
    /**
     * 参数值非法。
     */
    int PARAMETER_VALUE_ERROR = 11;
    /**
     * 转换文件格式失败
     */
    int FILE_CONVERT_FAIL = 12;
    /**
     * 目录不存在。
     */
    int FOLDER_NAME_NOT_EXIST = 13;
    int FILE_DOWNLOAD_EXCEPTION = 14;
    int FILE_NOT_EXIST = 15;
    int REST_NOT_FOUND = 16;
    int RESOURCE_IP_IS_NULL = 17;
    int HTTP_RESPONSE_ERROR_DETAIL = 18;
    int SSH_FAIL = 19;
    int REST_FAIL = 20;
    int move_file_failed_source_file_not_exist = 21;
    int move_file_failed_dest_path_not_exist = 22;
    int copy_file_failed_source_file_not_exist = 23;
    int copy_file_failed_dest_path_not_exist = 24;
    int report_data_error_need_report_again = 31;
    int UNAUTHORIZED  = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int HTTP_CLIENT_ERROR = 405;
    int Conflict = 409;
    int HTTP_UNSUPPORT_MEDIA_TYPE = 415;
    int SERVER_INTERNAL_ERROR = 500;
    int HOST_CONNECT_ERROR = 501;
    int BAD_GATEWAY = 502;
    int HOST_CONNECT_TIMEOUT = 503;
    int REST_CLIENT_ERROR = 504;
    int NETWORK_DIST_NOT_CONFIGURED = 505;

    int TASK_NOT_FOUND = 900;
    //采集节点部署：1001 - 1100
    int DEPLOY_NOT_COMPLETED = 1001;
    int OPERATE_NOT_SUPPORTED = 1002;
    int DEPLOY_ALREADY_CONFIG = 1003;
    int DEPLOY_VIP_USED = 1004;
    int NET_NOT_IN_RANGE = 1005;
    int MASTER_IP_ERROR = 1006;
    int DEPLOY_NODE_CONNECT_FAIL = 1007;
    int DEPLOY_NETWORK_WIFI_DHCP = 1008;
    int DEPLOY_ROUTE_PING_FAIL = 1009;
    int DEPLOY_ROUTE_NOT_FOUND_IP = 1010;
    int DEPLOY_NODE_NOT_INTEL_DEVICE = 1011;
    int DEPLOY_ROUTE_ADD_ERROR = 1012;
    int DEPLOY_ROUTE_ADD_MOST = 1013;
    int DEPLOY_NETWORK_NOT_EXIST = 1014;
    int DEPLOY_ROUTE_PING_VIA_FAIL = 1015;
    //参数配置：1101 - 1200
    int PARAMETER_NOT_EXIST = 1101;
    //定时任务：1201 - 1300
    //登录认证租户上报：1301 - 1400
    int MODIFY_USER_ERROR = 1301;
    int USER_DOES_NOT_EXIT = 1302;
    int WRONG_PASSWORD = 1303;
    int TOKEN_ERROR = 1304;
    int NAME_PWD_IS_NULL = 1305;
    int PWD_ERR = 1306;
    int LOGIN_SUCCESS = 1307;
    int LOGIN_ERROR = 1308;
    int TOKEN_IS_NOT_NULL = 1309;
    int DATACENTER_TOKEN_ERROR = 1310;
    int DATACENTER_CONFIG_ERROR = 1311;
    int DATACENTER_AUTH_ERROR = 1312;
    int The_password_can_contain = 1313;
    int The_password_does_not_meet_the_complexity_requirements1 = 1314;
    int The_password_does_not_meet_the_complexity_requirements2 = 1315;
    int The_password_does_not_meet_the_complexity_requirements3 = 1316;
    int The_password_does_not_meet_the_complexity_requirements4 = 1317;
    int DATACENTER_AUTH_HTTP_ERROR = 1318;
    int SSH_WEBSOCKET_CREATE_ERROR = 1319;
    int DATACENTER_AUTH_TENANT_ERROR = 1320;
    //Workspace数据收集：1401 - 1500
    int RESTHOST_NONE = 1401;
    int RESTHOST_PARAM_ERROR = 1402;
    int RESTHOST_RESOURCEID_NONE = 1403;
    int RESTHOST_RESOURCE_NONE = 1404;
    int METRIC_NONE = 1405;
    //CAS数据收集：1501 - 1600
    //UIS数据收集：1601 - 1700
    //日志实时上报：1701 - 1800
    //HA异常：1801 - 1900
    int NO_NETWORK_INTERFACE = 1801;
    int CONFIG_NOT_FOUND = 1802;
    int VIP_IS_USED = 1803;
    int MASK_INVALID = 1804;
    int PORT_CONNECT_ERROR = 1805;
    int BACKUP_DB_INIT_FAIL = 1806;
    int BACKUP_CONNECT_ERROR = 1807;
    int BACKUP_SERVICE_DOWN = 1808;
    int CONSUME_BY_BACKUP = 1809;
    // 命令行下发 1901-2000
    int SSH_COMMAND_SSHCHANNEL_CONNECTED_TIMEOUT = 1901;
    // 远程命令下发 2001-2010
    int OPERATE_COMMAND_NOT_SUPPORTED = 2001;
    // 资源下发 2011-2020
    int RESOURCE_PASSWORD_OR_SERVER_PASSWORD_EMPTY = 2011;
    //巡检工具下载失败
    int INSPECT_TOOLS_DOWNLOAD_FAIL =1902;
    int INSPECT_RESULT_FAIL=1903;
    int FILE_GET_BYTE_FAIL=1904;
    int INSPECT_FAIL=1905;
    int INSPECT_NOT_SUPPORT=1906;
    int INSPECT_OVERDUE=1907;
    int INSPECT_WORKSPACE_ROUTINE_CHECK_FAIL=1908;
    int INSPECT_SELECT_HOST_FAIL=1909;

    //Agent升级失败
    int ONLINE_UPGRADE_FAIL=1915;
    int UPGRADE_TOOLS_DOWNLOAD_FAIL =1916;
    // 采集端自身运维 2021-2030
    int WATCHER_CPU_USAGE_FAIL = 2021;
    int WATCHER_MEM_USAGE_FAIL = 2022;
    int WATCHER_STORAGE_USAGE_FAIL = 2023;
    int WATCHER_COMPONENT_CPU_USAGE_FAIL = 2024;
    int WATCHER_COMPONENT_MEM_USAGE_FAIL = 2025;

    //watcher 远程命令行用户确认
    int WATCHER_CHECK_USER_SSH_AUTH_REASION =1919;
    int WATCHER_CHECK_USER_SSH_AUTH =1920;
    int WATCHER_CHECK_USER_SSH_AUTH_FAIL =1921;
    int MODIFY_SSH_AUTH_FAIL =1922;
    int REOSURE_NO_USABLE =1923;

    int RESOURCE_EXCEPTION_REASION =  3001;


    static String getErrorMessage(Integer errorCode, Object... args) {
        return stringManager.getString(PREFIX + (Objects.isNull(errorCode) ? DEFAULT_UNDEFINED_ERROR : errorCode), args);
    }
}
