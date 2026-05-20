package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ClusterBasicDTO {
    @Schema(description = "集群id", example = "8")
    private Long id;
    @Schema(description = "集群名称", example = "集群1")
    private String name;
    @Schema(description = "描述", example = "1")
    private String description;
    @Schema(description = "是否启用HA  0:不启用HA 1:启用HA", example = "1")
    private Integer ha;
}
