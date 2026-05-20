package com.virtual.cloud.om.agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @Author: w22798
 * @Date: 2022/4/26 19:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//相当于数据库里的表名
@Schema
public class ResourceEntity {
    @Schema(description = ("资源类型"))
    private String platform;
    @Schema(description = ("资源ID"))
    
    private String id;
    @Schema(description = ("IP地址"))
    private String ipAddress;
    @Schema(description = ("端口"))
    private Integer port;
    @Schema(description = ("rest认证用户名"))
    private String ac;
    @Schema(description = ("rest认证用户密码"))
    private String ci;
    @Schema(description = ("访问协议HTTP/HTTPS"))
    private String protocol;
    @Schema(description = ("认证类型，目前默认统一为Digest"))
    private String authType;
    @Schema(description = ("管理节点用户一般为root"))
    private String serverUsername;
    @Schema(description = ("管理节点密码"))
    private String serverPassword;
    @Schema(description = ("管理节点端口号"))
    private Integer serverPort;
    @Schema(description = ("是否激活状态：0-否，1-是"))
    private Integer active;
    @Schema(description = ("创建时间"))
    private String createTime;
    @Schema(description = ("更新时间"))
    private String updateTime;
}
