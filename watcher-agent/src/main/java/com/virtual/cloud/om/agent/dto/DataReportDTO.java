package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class DataReportDTO {
    @Schema(description = ("租户ID"))
    private String watcherCode;
    private String comCode;
    private String orgCode;
    @Schema(description = ("资源名称"))
    private ReportResourceEnum platform;
    @Schema(description = ("数组类型，包含多个上报数据"))
    private List<ReportDTO> data;
    @Schema(description = ("上报时间"))
    private Long reportTimestamp;
    @Schema(description = ("跟踪ID"))
    private String traceId;
}
