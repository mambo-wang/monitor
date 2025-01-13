package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/14 19:36
 */
@Data
@ApiModel("巡检上传")
public class InspectUploadDTO implements Serializable {

    private static final long serialVersionUID = 5395695588580393747L;
    @ApiModelProperty("巡检结果MD5")
    private String identifier;
    @ApiModelProperty("巡检记录id,确定唯一一次巡检")
    private Long inspectRecordId;

    private File file;
}
