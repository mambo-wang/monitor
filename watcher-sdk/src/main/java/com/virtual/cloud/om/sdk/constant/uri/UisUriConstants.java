package com.virtual.cloud.om.sdk.constant.uri;

/**
 * @Author: w22798
 * @Date: 2022/5/05 14:25
 */
public interface UisUriConstants {

    String UIS_NAME = "/uis/uis";
    String CAS_NAME = "/cas";


    String TEST_CONNECTION = "/uis/login/test";
    // uis管理平台页面录接口
    // encrypt=false时用户名密码传明文，=true时DES加密
    String LOGIN = "/uis/spring_check?encrypt=true&loginType=authorCenter&name=%s&password=%s";

    String HOST_WAKE = UIS_NAME + "/host/%s/wake";
    String HOST_SHUTOFF = UIS_NAME + "/host/%s/shutoff";
    String HOST_REBOOT = UIS_NAME + "/host/%s/reboot";
    String HOST_INTO_MAINTAIN = UIS_NAME + "/host/intoMaintainStatus";
    String HOST_EXIT_MAINTAIN = UIS_NAME + "/host/exitMaintainStatus";
    String HOST_INFO_LIST = UIS_NAME + "/host/queryHostInfo?offset=0&limit=-1";
    String TASK_MESSAGE = UIS_NAME + "/message/%s";
    String PLATFORM_VERSION = UIS_NAME + "/host/queryVersion";
    /**
     * 是否双机热备
     */
    String UIS_ISDOUBLECVM = UIS_NAME + "/cvmManage/isDoubleCvm";
    /**
     *双机热备获取主节点信息
     */
    String UIS_HOTSTANDBYINFO = UIS_NAME + "/cvmManage/hotStandbyInfo";

    /**
     * 认证相关
     */
    interface Oauth {
        /**
         * 管理员token操作 1、POST 获取 2、GET 验证 3、DELETE 删除
         */
        String OAUTH_TOKEN = "/oauth2/token";
        /**
         * 删除client_id关联的所有token
         */
        String OAUTH_TOKEN_DELETE_BY_ID = "/oauth2/client/token";
        /**
         * 根据client_id查询关联的所有token的总数
         */
        String OAUTH_TOKEN_COUNT_QUERY_BY_ID = "/oauth2/client/token/count";
    }

    interface Warn {
        String WARN_MANAGE_LIST_URL = UIS_NAME + "/warnManage/realTimeAlarms?offset=%d&limit=%d&sortDir=%d&sortField=%s";

        String WARN_UIS_REAL_TIME_ALARMS = UIS_NAME + "/warnManage/realTimeAlarms?eventTime_from=%s&eventTime_to=%s&state=%s&offset=%s&limit=%s";
    }

    /**
     * 操作员信息
     */
    interface Operator {
        String QUERY_UIS_OPERATOR = "/uis/operator?limit=%d&offset=%d";
    }

    /** 获取日志*/
    interface Log{
        String GATHER_LOG = UIS_NAME + "/operationlog/gatherLog";
        String MESSAGE_QUERY = UIS_NAME + "/message/%s";
        String DOWNLOAD_LOGFILE = UIS_NAME + "/download/logfile";
        String OPERATION_LOGS_VM = UIS_NAME + "/operationlog/queryOperationLogByCondition?limit=%d&offset=%d&category=%d";
    }
    interface SshAuth{
        /**
         * SystemMgr.ParamSet.SystemParam是否包含
         */
        String CHECK_USER_SSH_AUTH ="/uis/spring_check";
        /**
         * 查询是否开启
         */
        String QUERY_ENABLE_SSH ="/uis/systemConfig/querySysConfigEnableSSH";
        /**
         * 修改开启
         */
        String MODIFY_ENABLE_SSH ="/uis/systemConfig/modifyEnableSSH";
}

}

