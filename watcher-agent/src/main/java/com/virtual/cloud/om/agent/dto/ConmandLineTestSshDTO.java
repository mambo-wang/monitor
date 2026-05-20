package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ConmandLineTestSshDTO {
    @Schema(description = ("测试链接唯一"))
    private String uuid;
    @Schema(description = ("ssh username"))
    private String username;
    @Schema(description = ("ssh pwd"))
    private String password;
    @Schema(description = ("ssh 主机id"))
    private Integer hostId;
    @Schema(description = ("ssh 端口号"))
    private Integer port;
    @Schema(description = ("ssh 资源平台id"))
    private String resourceId;
    private String watcherIp;
    private String userId;
}
