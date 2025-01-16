package com.virtual.cloud.om.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ConmandLineSshDTO {
    @ApiModelProperty("websocket连接标识")
    private String ticket;
    @ApiModelProperty("ssh username")
    private String username;
    @ApiModelProperty("ssh pwd")
    private String password;
    @ApiModelProperty("websocket 连接超时时长 ms")
    private Integer timeout;
    @ApiModelProperty("ssh 主机id")
    private Integer hostId;
    @ApiModelProperty("ssh 端口号")
    private Integer port;
    @ApiModelProperty("ssh 资源平台id")
    private String resourceId;
    private String watcherIp;
    private String userId;
}
