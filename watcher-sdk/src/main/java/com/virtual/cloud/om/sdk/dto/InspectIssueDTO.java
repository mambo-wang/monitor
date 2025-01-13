package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/11 14:44
 */

@Data
@ApiModel("巡检指令下发")
public class InspectIssueDTO implements Serializable {
    private static final long serialVersionUID = 3745214104971329972L;

    @ApiModelProperty("资源Id")
    private String resourceId;
    @ApiModelProperty("巡检类型")
    private String inspectType;
    @ApiModelProperty("确定唯一一次巡检")
    private Long toolKitId;
    @ApiModelProperty("巡检记录id")
    private Long inspectRecordId;
    @ApiModelProperty("巡检工具MD5")
    private String identifier;
}
