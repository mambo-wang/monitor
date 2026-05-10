package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户详情")
public class SysUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号-不加密")
    private String id;

    @ApiModelProperty(value = "账号-不加密")
    private String username;

    @ApiModelProperty(value = "用户状态: active-已激活/inactive-未激活")
    private String status;

    @ApiModelProperty(value = "密码-加密")
    private String password;

}