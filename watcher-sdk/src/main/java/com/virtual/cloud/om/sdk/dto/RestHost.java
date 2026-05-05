package com.virtual.cloud.om.sdk.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("资源管理平台（cas/uis/workspace/onestor的管理平台服务）连接地址")
public class RestHost {

    /** 资源类型 workspace uis cas */
    @ApiModelProperty(value = "资源类型")
    private String platform;

    @ApiModelProperty(value = "资源id")
    private String resourceId;

    /** ip地址*/
    @ApiModelProperty(value = "主机IP地址")
    private String host;

    /** 协议类型*/
    @ApiModelProperty(value = "协议类型")
    private String protocol;

    /** 端口号*/
    @ApiModelProperty(value = "端口号")
    private Integer port;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "管理员账号")
    private String username;
    /** 密码*/
    @ApiModelProperty(value = "管理员密码")
    private String password;

    @ApiModelProperty("管理节点用户一般为root")
    private String serverUsername;
    @ApiModelProperty("管理节点密码")
    private String serverPassword;

    public RestHost() {
    }

    public RestHost(String platform, String resourceId, String host, String protocol, Integer port, String username, String password, String serverUsername, String serverPassword) {
        this.platform = platform;
        this.resourceId = resourceId;
        this.host = host;
        this.protocol = protocol;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serverUsername = serverUsername;
        this.serverPassword = serverPassword;
    }

    // Getters and Setters
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getServerUsername() { return serverUsername; }
    public void setServerUsername(String serverUsername) { this.serverUsername = serverUsername; }
    public String getServerPassword() { return serverPassword; }
    public void setServerPassword(String serverPassword) { this.serverPassword = serverPassword; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String platform;
        private String resourceId;
        private String host;
        private String protocol;
        private Integer port;
        private String username;
        private String password;
        private String serverUsername;
        private String serverPassword;

        public Builder platform(String platform) { this.platform = platform; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder host(String host) { this.host = host; return this; }
        public Builder protocol(String protocol) { this.protocol = protocol; return this; }
        public Builder port(Integer port) { this.port = port; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder serverUsername(String serverUsername) { this.serverUsername = serverUsername; return this; }
        public Builder serverPassword(String serverPassword) { this.serverPassword = serverPassword; return this; }

        public RestHost build() {
            RestHost host = new RestHost();
            host.setPlatform(this.platform);
            host.setResourceId(this.resourceId);
            host.setHost(this.host);
            host.setProtocol(this.protocol);
            host.setPort(this.port);
            host.setUsername(this.username);
            host.setPassword(this.password);
            host.setServerUsername(this.serverUsername);
            host.setServerPassword(this.serverPassword);
            return host;
        }
    }
}
