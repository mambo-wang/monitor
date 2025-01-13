package com.virtual.cloud.om.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/23 11:31
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class DataCenterDTO {

    @ApiModelProperty("用户名-不加密")
    private String username;

    @ApiModelProperty("密码-sm4加密")
    private String ci;

    @ApiModelProperty("公钥-base64编码")
    private String publicKey;
    private String ip;
    private Integer master;

    @ApiModelProperty("能力中心Agent唯一标识")
    private String watcherCode;
}
