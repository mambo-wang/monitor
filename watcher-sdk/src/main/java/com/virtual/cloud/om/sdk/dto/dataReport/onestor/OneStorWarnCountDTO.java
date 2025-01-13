package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "告警")
public class OneStorWarnCountDTO {
    @ApiModelProperty(value="count")
    private Integer count;
    @ApiModelProperty(value="major")
    private Integer major;
    @ApiModelProperty(value="warning")
    private Integer warning;
    @ApiModelProperty(value="critical")
    private Integer critical;
    @ApiModelProperty(value="minor")
    private Integer minor;
    @ApiModelProperty(value="self_define")
    private Integer self_define;

}
