package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class HealthInfoBasicDTO {
    private static final long serialVersionUID = 7630696172783821368L;

    /**
     * 物理机ID。 *
     */
    @Schema(description = "物理机ID")
    private Long hostId;

    /**
     * 主机健康度 *
     */
    @Schema(description = "主机健康度")
    private Long cvkHealth;


}
