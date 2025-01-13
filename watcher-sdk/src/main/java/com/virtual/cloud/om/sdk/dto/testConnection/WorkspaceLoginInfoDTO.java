package com.virtual.cloud.om.sdk.dto.testConnection;

/**
 * Created by x19765 on 2021/1/11.
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 操作员登录信息。
 *
 * @author NAME
 */
@Data
public class WorkspaceLoginInfoDTO implements Serializable {

    /**
     * 序列化ID。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 登录名。
     */
    private String loginName = null;

    /**
     * 登录密码。
     */
    private String pwd = null;

    /**
     * 登录地址。
     */
    private String loginIp = null;

    /**
     * 协议类型。
     */
    private String protocol = null;

    /**
     * 协议端口号。
     */
    private Integer port = null;
}
