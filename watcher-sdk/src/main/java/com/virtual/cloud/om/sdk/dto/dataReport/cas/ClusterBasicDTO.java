package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ClusterBasicDTO {
    @ApiModelProperty(value = "集群id", example = "8")
    private Long id;
    @ApiModelProperty(value = "集群名称", example = "集群1")
    private String name;
    @ApiModelProperty(value = "描述", example = "1")
    private String description;
    @ApiModelProperty(value = "是否启用HA  0:不启用HA 1:启用HA", example = "1")
    private Integer ha;
}
