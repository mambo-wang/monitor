package com.virtual.cloud.om.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/7/27 19:20
 */
@Data
@ApiModel
public class UpgradeDTO implements Serializable {
    private static final long serialVersionUID = -7890211834435775363L;
    @ApiModelProperty("升级包id，数据中心的记录")
    private Long packageId;
    @ApiModelProperty("升级包记录id，运维中心的记录")
    private Long recordId;

    private String md5;
    private String uuid;

    @ApiModelProperty("租户id")
    private String userId;

}
