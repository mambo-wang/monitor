package com.virtual.cloud.om.sdk.dto.logBatch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class LogBatchTargetsQueryDTO {
    private String title;
    private String id;
}
