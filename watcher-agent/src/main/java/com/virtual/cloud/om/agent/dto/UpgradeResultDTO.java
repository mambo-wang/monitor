package com.virtual.cloud.om.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/7/28 14:10
 */
@Data
@ApiModel
public class UpgradeResultDTO implements Serializable {
    private static final long serialVersionUID = 4758835525370683437L;
    @ApiModelProperty("升级记录id")
    private Long recordId;
    @ApiModelProperty("升级结果 0-失败，1-成功")
    private Integer status;
    @ApiModelProperty("原因")
    private String desc;
    @ApiModelProperty(value ="租户id")
    private String watcherCode;
    @ApiModelProperty(value = "升级包id")
    private Long packageId;
    @ApiModelProperty(value = "uuid")
    private String uuid;
    @ApiModelProperty(value = "userId")
    private String userId;

}
