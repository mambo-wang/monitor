package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拒绝注册申请请求VO
 */
@Data
@ApiModel(value = "拒绝注册申请请求")
public class UserRegisterRejectVO {

    @ApiModelProperty("申请ID")
    private String id;

    @ApiModelProperty("拒绝原因（可选）")
    private String rejectReason;
}
