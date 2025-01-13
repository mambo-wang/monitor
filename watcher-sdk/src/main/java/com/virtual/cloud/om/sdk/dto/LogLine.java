package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

@Data
public class LogLine {

    // 日志输出时间
    private String time;

    private Long timestamp;

    // 日志级别
    private String level;

    // 日志线程名称
    private String thread;

    // 请求uuid
    private String requestUuid;

    // ip
    private String requestIp;

    //端口号
    private String requestPort;

    // 方法
    private String method;

    // 行数
    private String line;

    // 日志内容
    private String message;

    //脚本信息
    private String script;

    //PID
    private String pid;

    //参数信息
    private String params;

    private String hostName;

    private String targetType;
    private String path;
    private String platform;
    private String resourceId;
    private String hostId;
    private String hostIp;

}
