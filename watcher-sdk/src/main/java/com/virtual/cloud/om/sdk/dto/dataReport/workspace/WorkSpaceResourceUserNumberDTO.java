package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class WorkSpaceResourceUserNumberDTO {
    @ApiModelProperty("用户总数")
    private Integer totalLength;

    private List data;

    private Integer errorCode;
}
