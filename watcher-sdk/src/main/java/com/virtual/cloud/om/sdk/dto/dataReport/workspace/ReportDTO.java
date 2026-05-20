package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ReportDTO {
    @Schema(description = ("采集的指标类型"))
    private ReportMetricEnum metric;
    @Schema(description = ("指标的值类型"))
    private ReportDataTypeEnum type;
    @Schema(description = ("标签，用于给各类资源打标"))
    private String tags;
    @Schema(description = ("上传批次"))
    private String batchNum;
    @Schema(description = "根据type决定value的值类型")
    private Object value;
    @Schema(description = ("时间戳"))
    private Long timestamp;
}
