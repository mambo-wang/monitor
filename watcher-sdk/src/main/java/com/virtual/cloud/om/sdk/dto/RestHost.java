package com.virtual.cloud.om.sdk.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
