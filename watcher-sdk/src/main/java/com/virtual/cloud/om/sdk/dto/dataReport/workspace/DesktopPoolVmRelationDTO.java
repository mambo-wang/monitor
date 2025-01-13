package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DesktopPoolVmRelationDTO {
    @ApiModelProperty(value = "桌面池id", example = "8")
    private Long desktopPoolId;
    @ApiModelProperty(value = "类型", example = "1")
    private Integer computerType;
    @ApiModelProperty(value = "虚拟机uuid", example = "测试集群")
    private String vmUuid;
}
