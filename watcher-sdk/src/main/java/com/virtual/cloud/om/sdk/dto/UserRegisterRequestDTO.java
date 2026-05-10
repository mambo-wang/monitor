package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户注册申请DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户注册申请")
public class UserRegisterRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "申请ID")
    private String id;

    @ApiModelProperty(value = "申请用户名")
    private String username;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态描述")
    private String statusDesc;

    @ApiModelProperty(value = "提交时间")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "审批时间")
    private LocalDateTime approveTime;

    @ApiModelProperty(value = "审批人")
    private String approver;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "累计被拒次数")
    private Integer rejectCount = 0;
}
