package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
public class ResourcesActiveDTO {
    @ApiModelProperty("平台资源相关信息")
    private ActiveResourceMessage activeResourceMessage;
    @ApiModelProperty("租户code")
    private String watcherCode;
    private Integer connectionStatus;
    private String operation;
    private String errorMessage;

    @Data
    @ApiModel
    @Accessors(chain = true)
    public static class ActiveResourceMessage{
        @ApiModelProperty("用户名")
        private String ac;
        @ApiModelProperty(value = "平台资源id",required = true)
        private String id;
        @ApiModelProperty(value = "ip",required = true)
        private String ipAddress;
        @ApiModelProperty("平台类型")
        private ReportResourceEnum platform;
        @ApiModelProperty("端口号")
        private Integer port;
        @ApiModelProperty("协议")
        private String protocol;
    }
}
