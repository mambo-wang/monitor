package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema
public class ResourcesActiveDTO {
    @Schema(description = ("平台资源相关信息"))
    private ActiveResourceMessage activeResourceMessage;
    @Schema(description = ("租户code"))
    private String watcherCode;
    private Integer connectionStatus;
    private String operation;
    private String errorMessage;

    @Data
    @Schema
    @Accessors(chain = true)
    public static class ActiveResourceMessage{
        @Schema(description = ("用户名"))
        private String ac;
        @Schema(description = "平台资源id",required = true)
        private String id;
        @Schema(description = "ip",required = true)
        private String ipAddress;
        @Schema(description = ("平台类型"))
        private ReportResourceEnum platform;
        @Schema(description = ("端口号"))
        private Integer port;
        @Schema(description = ("协议"))
        private String protocol;
    }
}
