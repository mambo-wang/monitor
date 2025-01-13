package com.virtual.cloud.om.sdk.config.token;

import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * CAS服务器
 */
public class PublicCloud implements Serializable {

    private static final long serialVersionUID = -153649597761606298L;


    /**
     * CVM。
     */
    public static final int CVM = 2;

    /**
     * 公有云名称。
     */
    private String name;

    /**
     * 公有云描述。 *
     */
    private String description;

    /**
     * 公有云IP地址。*
     */
    private String uri;

    /**
     * 用户名。 *
     */
    private String username;

    /**
     * 密码。*
     */
    private String password;

    /**
     * 协议类型(http, https)
     */
    private String protocal = null;

    /**
     * 端口
     */
    private Integer port = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return StringUtils.isEmpty(password) ? "" : SM4Utils.webDecryptText(password);
    }

    public void setPassword(String password) {
        this.password = StringUtils.isEmpty(password) ? "" : SM4Utils.webEncryptText(password);
    }

    public String getProtocal() {
        return protocal;
    }

    public void setProtocal(String protocal) {
        this.protocal = protocal;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
