package com.virtual.cloud.om.agent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//相当于数据库里的表名
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private String id;

    @ApiModelProperty(value = "账号-不加密")
    private String username;

    @ApiModelProperty(value = "密码-加密")
    private String password;

    /** 最近一次登录时间。 */
    private Date lastLoginTime;

}
