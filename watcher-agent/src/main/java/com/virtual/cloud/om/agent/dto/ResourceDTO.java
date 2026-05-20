package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ResourceDTO {
    @Schema(description = ("资源名称"))
    private String resourceName;
    @Schema(description = ("资源类型: cas/uis/workspace"))
    private String platform;
    @Schema(description = ("资源ID, 不能重复，非必填"))
    private String id;
    @Schema(description = ("IP地址"))
    private String ipAddress;
    @Schema(description = ("HTTP端口"))
    private Integer port;
    @Schema(description = ("HTTP接口认证用户名"))
    private String ac;
    @Schema(description = ("HTTP接口认证用户密码"))
    private String ci;
    @Schema(description = ("访问协议HTTP/HTTPS"))
    private String protocol;
    @Schema(description = ("HTTP接口认证类型，目前默认统一为Digest"))
    private String authType;
    @Schema(description = ("服务器后台账号一般为root"))
    private String serverUsername;
    @Schema(description = ("服务器后台密码"))
    private String serverPassword;
    @Schema(description = ("服务器后台SSH端口号"))
    private Integer serverPort;
}
