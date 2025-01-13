package com.virtual.cloud.om.sdk.constant.uri;

/**
 * @Author: w22798
 * @Date: 2022/5/5 16:09
 */
public interface DataCenterUriConstants {

    String PREFIX = "/api";

    String PREFIX_URI = "/oad-center";

    /** 日志实时上报 */
    String REAL_TIME_LOG_UPLOAD = PREFIX + PREFIX_URI + "/report/realtime-logs";

    /**登录获取token*/
    String LOGIN_DATACENTER = PREFIX_URI + "/auth";
    /**
     * 采集器查询纳管资源
     */
    String QUERY_RESOURCE = PREFIX + PREFIX_URI + "/strategy/resource/{watcherCode}";
    /**
     * 激活资源接口
     */
    String QUERY_RESOURCES_ACTIVE = PREFIX + PREFIX_URI + "/resources/active";

    /**
     * 数据上报
     */
    String REPORT_METRIC = PREFIX + PREFIX_URI + "/report/metric";
    /**
     * 上报异常
     */
    String REPORT_ERROR = PREFIX + PREFIX_URI + "/report/error";

    /** 日志实时采集策略拉取 */
    String QUERY_REALTIMELOG_STRATEGY = PREFIX + PREFIX_URI + "/strategy/realtime-logs/%s?resourceIds=";
    /**
     * 策略主动拉取
     */
    String DATA_CENTER_URI = PREFIX + PREFIX_URI + "/strategy/schedule/%s?resourceIds=";

    /**
     * 告警策略主动拉取
     */
    String WARN_DATA_CENTER_URI = PREFIX + PREFIX_URI + "/strategy/warn/%s?resourceIds=";
    /**
     * 批量日志收集
     */
    String LOG_BATCH_UPLOAD_URI = PREFIX_URI + "/log/chunk-logs";
    /**
     * 批量日志收集失败上报
     */
    String LOG_BATCH_UPLOAD_FAIL_URI = PREFIX_URI + "/log/reportFail";
    /**
     * 批量日志文件检查
     */
    String LOG_BATCH_UPLOAD_CHECK_URI = PREFIX_URI + "/log/upload?watcherCode=%s&fileName=%s&ticket=%s&range=%s&platform=%s";

    /**
     * 巡检工具下载
     */
    String INSPECTION_ISSUED = PREFIX_URI + "/inspect/downloadToolKit/%s";

    /**
     * 巡检失败原因
     */
    String INSPECTION_UPLOAD_FAIL_MASSAGE = PREFIX_URI+"/inspect/inspectResult";
    /**
     * 巡检报告上传
     */
    String INSPECTION_REPORT_UPLOAD = PREFIX_URI + "/inspect/resultUpload";

    /**
     * 告警信息上报
     */
    String WARN_REPORT = PREFIX + PREFIX_URI + "/report/realtime-alarms";
    /**
     * 命令下发结果上报
     */
    String OPERATE_REPORT = PREFIX + PREFIX_URI + "/report/operateResult";
    /**
     * Agent在线升级升级包下载地址/oad-center/watcher/download/3
     */
    String ONLINE_UPGRADE =  PREFIX_URI + "/watcher/download/%s";
    /**
     * Agent在线升级升级结果反馈
     */
    String ONLINE_UPGRADE_RESULT = PREFIX + PREFIX_URI + "/report/watcher/upgradeResult";



}
