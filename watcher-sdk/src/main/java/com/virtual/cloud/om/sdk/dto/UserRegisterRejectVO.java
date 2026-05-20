package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 拒绝注册申请请求VO
 */
@Data
@Schema(description = "拒绝注册申请请求")
public class UserRegisterRejectVO {

    @Schema(description = ("申请ID"))
    private String id;

    @Schema(description = ("拒绝原因（可选）"))
    private String rejectReason;
}
