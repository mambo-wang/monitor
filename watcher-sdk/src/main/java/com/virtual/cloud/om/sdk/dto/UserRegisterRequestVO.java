package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户注册申请列表项VO
 */
@Data
@Schema(description = "用户注册申请列表项VO")
public class UserRegisterRequestVO {

    @Schema(description = ("申请ID"))
    private String id;

    @Schema(description = ("申请用户名"))
    private String username;

    @Schema(description = ("申请状态: pending-待审批/approved-已通过/rejected-已拒绝"))
    private String status;

    @Schema(description = ("提交时间"))
    private String submitTime;

    @Schema(description = ("审批人"))
    private String approver;

    @Schema(description = ("审批时间"))
    private String approveTime;

    @Schema(description = ("拒绝原因"))
    private String rejectReason;

    @Schema(description = ("备注"))
    private String remark;

    @Schema(description = ("累计被拒次数"))
    private Integer rejectCount;
}
