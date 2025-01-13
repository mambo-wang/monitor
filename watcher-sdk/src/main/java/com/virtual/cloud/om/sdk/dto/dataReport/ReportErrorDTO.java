package com.virtual.cloud.om.sdk.dto.dataReport;

import com.virtual.cloud.om.sdk.constant.report.ReportErrorTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ReportErrorDTO {
    private String watcherCode;
    private String comCode;
    private String orgCode;
    private String platform;
    private ReportErrorTypeEnum type;
    private String tags;
    private Integer errorCode;
    private String errorMessage;
    private Long reportTimestamp;
    private String traceId;
}
