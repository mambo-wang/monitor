package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/1 15:59
 */
@Data
public class ResourceRemoteDTO implements Serializable {
    private static final long serialVersionUID = -4759269218321699428L;

    private String resourceId;
    private String resourceName;
    private String ipAddress;
    private String platform;
    /**
     * 0 关闭  1开启
     */
    private Integer remote;
    private Long endTime;

    /**
     *
     */
    private String username;
    /**
     *
     */
    private String password;
    /**
     * 开启时间 0 一天 1 3天  2 7天 为-1表示不开启
     */
    private Integer remoteType;
    /**
     * 结束时间字符串
     */
    private String endTimeStr;

    /**
     * 资源是否异常状态 0-不可用 1-可用
     */
    private Integer usable;
}
