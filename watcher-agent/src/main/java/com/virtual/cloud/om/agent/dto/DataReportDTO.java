package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class DataReportDTO {
    @ApiModelProperty("租户ID")
    private String watcherCode;
    private String comCode;
    private String orgCode;
    @ApiModelProperty("资源名称")
    private ReportResourceEnum platform;
    @ApiModelProperty("数组类型，包含多个上报数据")
    private List<ReportDTO> data;
    @ApiModelProperty("上报时间")
    private Long reportTimestamp;
    @ApiModelProperty("跟踪ID")
    private String traceId;
}
