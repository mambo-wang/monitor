package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ResourceDTO {
    @ApiModelProperty("资源类型")
    private ReportResourceEnum platform;
    @ApiModelProperty("资源ID")
    private String id;
    @ApiModelProperty("IP地址")
    private String ipAddress;
    @ApiModelProperty("端口")
    private Integer port;
    @ApiModelProperty("rest认证用户名")
    private String ac;
    @ApiModelProperty("rest认证用户密码")
    private String ci;
    @ApiModelProperty("访问协议HTTP/HTTPS")
    private String protocol;
    @ApiModelProperty("认证类型，目前默认统一为Digest")
    private String authType;
    @ApiModelProperty("管理节点用户一般为root")
    private String serverUsername;
    @ApiModelProperty("管理节点密码")
    private String serverPassword;
    @ApiModelProperty("管理节点端口号")
    private Integer serverPort;
}
