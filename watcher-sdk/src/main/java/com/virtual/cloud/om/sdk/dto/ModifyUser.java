package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/4/28 16:02
 */
@ApiModel("改密")
@Data
public class ModifyUser {
    @ApiModelProperty(value = "账号-不加密")
    private String username;

    @ApiModelProperty(value = "老密码-加密")
    private String oldPassword;

    @ApiModelProperty(value = "新密码-加密")
    private String newPassword;

    @ApiModelProperty(value = "确认新密码-加密")
    private String renewPassword;
}
