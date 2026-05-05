package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/4/26 20:20
 */
@ApiModel("节点状态信息")
public class DeployQueryVO{

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "虚IP地址")
    private String vip;

    @ApiModelProperty(value = "账号-不加密")
    private String username;

    @ApiModelProperty(value = "密码-加密")
    private String password;

    @ApiModelProperty(value = "是否为主节点")
    private Boolean isMaster;

    @ApiModelProperty(value = "组件状态信息")
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
