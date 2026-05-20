package com.virtual.cloud.om.sdk.dto.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class DataValueAndTagsDTO {
    @Schema(description = "data.value字段",required = true)
    private Object value;
    @Schema(description = "data.tags字段",required = true)
    private String tags;
    @Schema(description = "data.timestamp字段",required = true)
    private Long timestamp;
}
