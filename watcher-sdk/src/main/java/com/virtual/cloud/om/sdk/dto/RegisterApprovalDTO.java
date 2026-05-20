package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 审批请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "审批请求")
public class RegisterApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    private String id;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "备注")
    private String remark;
}
