package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class WarnEndFromAndEndToDTO {
    @ApiModelProperty("开始时间")
    private Long endFrom;
    @ApiModelProperty("结束时间")
    private Long endTo;
    @ApiModelProperty("上报时间")
    private Long reportTime;

}
