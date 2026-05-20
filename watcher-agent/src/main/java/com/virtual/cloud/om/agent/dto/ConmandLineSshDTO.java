package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ConmandLineSshDTO {
    @Schema(description = ("websocket连接标识"))
    private String ticket;
    @Schema(description = ("ssh username"))
    private String username;
    @Schema(description = ("ssh pwd"))
    private String password;
    @Schema(description = ("websocket 连接超时时长 ms"))
    private Integer timeout;
    @Schema(description = ("ssh 主机id"))
    private Integer hostId;
    @Schema(description = ("ssh 端口号"))
    private Integer port;
    @Schema(description = ("ssh 资源平台id"))
    private String resourceId;
    private String watcherIp;
    private String userId;
}
