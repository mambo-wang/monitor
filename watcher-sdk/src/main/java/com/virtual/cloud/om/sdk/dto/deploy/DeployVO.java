package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: w22798
 * @Date: 2022/4/26 19:54
 */
@ApiModel("节点部署")
@Data
public class DeployVO {

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "账号-不加密")
    private String username;

    @ApiModelProperty(value = "密码-加密")
    private String password;

    @ApiModelProperty(value = "是否为主节点")
    private Boolean isMaster;
}
