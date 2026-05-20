package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class WorkSpaceResourceUserNumberDTO {
    @Schema(description = ("用户总数"))
    private Integer totalLength;

    private List data;

    private Integer errorCode;
}
