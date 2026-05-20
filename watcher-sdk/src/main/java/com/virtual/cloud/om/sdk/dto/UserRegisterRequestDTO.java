package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "用户注册申请")
public class UserRegisterRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    private String id;

    @Schema(description = "申请用户名")
    private String username;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批人")
    private String approver;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "累计被拒次数")
    private Integer rejectCount = 0;
}
