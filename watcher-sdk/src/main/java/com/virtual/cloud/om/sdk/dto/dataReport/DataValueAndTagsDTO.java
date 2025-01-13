package com.virtual.cloud.om.sdk.dto.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DataValueAndTagsDTO {
    @ApiModelProperty(value = "data.value字段",required = true)
    private Object value;
    @ApiModelProperty(value = "data.tags字段",required = true)
    private String tags;
    @ApiModelProperty(value = "data.timestamp字段",required = true)
    private Long timestamp;
}
