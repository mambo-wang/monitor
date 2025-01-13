package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ReportDTO {
    @ApiModelProperty("采集的指标类型")
    private ReportMetricEnum metric;
    @ApiModelProperty("指标的值类型")
    private ReportDataTypeEnum type;
    @ApiModelProperty("标签，用于给各类资源打标")
    private String tags;
    @ApiModelProperty("上传批次")
    private String batchNum;
    @ApiModelProperty("根据type决定value的值类型")
    private Object value;
    @ApiModelProperty("时间戳")
    private Long timestamp;
}
