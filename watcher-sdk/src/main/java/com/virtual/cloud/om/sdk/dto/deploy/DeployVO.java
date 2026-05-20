package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Author: w22798
 * @Date: 2022/4/26 19:54
 */
@Schema(description = "节点部署")
public class DeployVO {

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "账号-不加密")
    private String username;

    @Schema(description = "密码-加密")
    private String password;

    @Schema(description = "是否为主节点")
    private Boolean isMaster;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getIsMaster() { return isMaster; }
    public void setIsMaster(Boolean isMaster) { this.isMaster = isMaster; }
}
