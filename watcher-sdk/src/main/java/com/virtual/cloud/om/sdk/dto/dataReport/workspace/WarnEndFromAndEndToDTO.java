package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class WarnEndFromAndEndToDTO {
    @Schema(description = ("开始时间"))
    private Long endFrom;
    @Schema(description = ("结束时间"))
    private Long endTo;
    @Schema(description = ("上报时间"))
    private Long reportTime;

}
