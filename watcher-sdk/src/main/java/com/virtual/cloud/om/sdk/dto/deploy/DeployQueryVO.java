package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/4/26 20:20
 */
@Schema(description = "节点状态信息")
public class DeployQueryVO{

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "虚IP地址")
    private String vip;

    @Schema(description = "账号-不加密")
    private String username;

    @Schema(description = "密码-加密")
    private String password;

    @Schema(description = "是否为主节点")
    private Boolean isMaster;

    @Schema(description = "组件状态信息")
    private List<Component> components;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getVip() { return vip; }
    public void setVip(String vip) { this.vip = vip; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getIsMaster() { return isMaster; }
    public void setIsMaster(Boolean isMaster) { this.isMaster = isMaster; }
    public List<Component> getComponents() { return components; }
    public void setComponents(List<Component> components) { this.components = components; }
}
