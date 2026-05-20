package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户详情")
public class SysUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账号-不加密")
    private String id;

    @Schema(description = "账号-不加密")
    private String username;

    @Schema(description = "用户状态: active-已激活/inactive-未激活")
    private String status;

    @Schema(description = "密码-加密")
    private String password;

}