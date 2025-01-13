package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/1 15:33
 */
@Data
public class ResourceRemoteAuthDTO implements Serializable {

    private static final long serialVersionUID = -5414196468594715486L;
    /**
     * 用户id（租户id）
     */
    private  String userId;
    private String resourceId;
    private String username;
    private String password;
    /**
     * 0 1 2 为-1 表示不开启 0：1天 1：3天 2 7天
     */
    private  Integer remoteType;

    private String uuid;

    private String user;

    private Boolean authCode;

}
