package com.virtual.cloud.om.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DataCenterConfigDTO {

    private String ip;

    @ApiModelProperty("用户名-不加密")
    private String username;

    @ApiModelProperty("密码-加密")
    private String password;

    @ApiModelProperty("机构名称")
    private String comName;

    @ApiModelProperty("组织名称")
    private String orgName;
}
