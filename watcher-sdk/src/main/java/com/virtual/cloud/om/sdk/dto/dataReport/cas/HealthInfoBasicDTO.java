package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class HealthInfoBasicDTO {
    private static final long serialVersionUID = 7630696172783821368L;

    /**
     * 物理机ID。 *
     */
    @ApiModelProperty(value = "物理机ID")
    private Long hostId;

    /**
     * 主机健康度 *
     */
    @ApiModelProperty(value = "主机健康度")
    private Long cvkHealth;


}
