package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class WarnDataDTO {
    @Schema(description = ("告警类型"))
    private Integer type;
    @Schema(description = ("告警来源"))
    private String src;
    @Schema(description = ("告警名称"))
    private String name;
    @Schema(description = ("告警对象类别"))
    private Integer objectType;
    @Schema(description = ("告警信息内容"))
    private String message;
    @Schema(description = ("最新告警时间"))
    private Long endsAt;
    @Schema(description = ("首次告警时间"))
    private Long startsAt;
    @Schema(description = "告警级别")
    private Integer level;
    @Schema(description = "告警重复次数")
    private Integer count;
    @Schema(description = ("资源id"))
    private String resourceId;
    @Schema(description = ("能力中心Agent-ip"))
    private String watcherIp;
}
