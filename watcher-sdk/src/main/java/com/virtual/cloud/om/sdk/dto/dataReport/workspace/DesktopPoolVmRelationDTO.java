package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class DesktopPoolVmRelationDTO {
    @Schema(description = "桌面池id", example = "8")
    private Long desktopPoolId;
    @Schema(description = "类型", example = "1")
    private Integer computerType;
    @Schema(description = "虚拟机uuid", example = "测试集群")
    private String vmUuid;
}
