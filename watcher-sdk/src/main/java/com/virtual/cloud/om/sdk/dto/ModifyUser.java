package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/4/28 16:02
 */
@Schema(description = "改密")
@Data
public class ModifyUser {
    @Schema(description = "账号-不加密")
    private String username;

    @Schema(description = "老密码-加密")
    private String oldPassword;

    @Schema(description = "新密码-加密")
    private String newPassword;

    @Schema(description = "确认新密码-加密")
    private String renewPassword;
}
