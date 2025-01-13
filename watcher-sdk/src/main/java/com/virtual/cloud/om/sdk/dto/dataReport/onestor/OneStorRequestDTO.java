package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "告警条件")
public class OneStorRequestDTO {
    @ApiModelProperty(value="alarm_level")
    private List alarm_level;
    @ApiModelProperty(value="alarm_module")
    private List alarm_module;
    @ApiModelProperty(value="alarm_sort")
    private String alarm_sort = "desc";
    @ApiModelProperty(value="alarm_state")
    private String alarm_state = "current";
    @ApiModelProperty(value="alarm_status")
    private String[] alarm_status;
    @ApiModelProperty(value="limit")
    private Integer limit = 100;
    @ApiModelProperty(value="offset")
    private Integer offset = 0;
    @ApiModelProperty(value="begin_time")
    private Long begin_time;
    @ApiModelProperty(value="end_time")
    private Long end_time;
    @ApiModelProperty(value="content_key")
    private String content_key;
    @ApiModelProperty(value="nodepool_name")
    private List nodepool_name;
    @ApiModelProperty(value="language")
    private String language = "zh-cn";
}
