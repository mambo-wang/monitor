package com.virtual.cloud.om.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ResourceDTO {
    @ApiModelProperty("资源名称")
    private String resourceName;
    @ApiModelProperty("资源类型: cas/uis/workspace/onestor")
    private String platform;
    @ApiModelProperty("资源ID, 不能重复，非必填")
    private String id;
    @ApiModelProperty("IP地址")
    private String ipAddress;
    @ApiModelProperty("HTTP端口")
    private Integer port;
    @ApiModelProperty("HTTP接口认证用户名")
    private String ac;
    @ApiModelProperty("HTTP接口认证用户密码")
    private String ci;
    @ApiModelProperty("访问协议HTTP/HTTPS")
    private String protocol;
    @ApiModelProperty("HTTP接口认证类型，目前默认统一为Digest")
    private String authType;
    @ApiModelProperty("服务器后台账号一般为root")
    private String serverUsername;
    @ApiModelProperty("服务器后台密码")
    private String serverPassword;
    @ApiModelProperty("服务器后台SSH端口号")
    private Integer serverPort;
}
