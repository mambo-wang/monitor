package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户注册申请列表项VO
 */
@Data
@ApiModel(value = "用户注册申请列表项VO")
public class UserRegisterRequestVO {

    @ApiModelProperty("申请ID")
    private String id;

    @ApiModelProperty("申请用户名")
    private String username;

    @ApiModelProperty("申请状态: pending-待审批/approved-已通过/rejected-已拒绝")
    private String status;

    @ApiModelProperty("提交时间")
    private String submitTime;

    @ApiModelProperty("审批人")
    private String approver;

    @ApiModelProperty("审批时间")
    private String approveTime;

    @ApiModelProperty("拒绝原因")
    private String rejectReason;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("累计被拒次数")
    private Integer rejectCount;
}
