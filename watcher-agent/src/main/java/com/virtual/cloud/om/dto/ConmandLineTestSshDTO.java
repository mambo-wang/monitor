package com.virtual.cloud.om.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ConmandLineTestSshDTO {
    @ApiModelProperty("测试链接唯一")
    private String uuid;
    @ApiModelProperty("ssh username")
    private String username;
    @ApiModelProperty("ssh pwd")
    private String password;
    @ApiModelProperty("ssh 主机id")
    private Integer hostId;
    @ApiModelProperty("ssh 端口号")
    private Integer port;
    @ApiModelProperty("ssh 资源平台id")
    private String resourceId;
    private String watcherIp;
    private String userId;
}
