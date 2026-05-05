package com.virtual.cloud.om.agent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PwdStrategy implements Serializable {
    private static final long serialVersionUID = -2868853313388553283L;

    
    private String id;

    /**
     * 允许密码字符长度
     */
    @ApiModelProperty(value = "允许密码最小长度", example = "6")
    private Integer minLength;

    /**
     *密码有效期(天) 0表示永久有效 默认值：0
     */
    @ApiModelProperty(value = "密码有效期(天) 0表示永久有效 默认值：0", example = "0")
    private Integer pwdLifeTime;

    /**
     * 密码是否必须包含特殊字符 1：包含，0：不包含
     */
    @ApiModelProperty(value = "密码复杂性要求：0：不做特殊要求，1：必须混合使用字母数字，2：必须包含特殊字符，3：必须为字母数字特殊字符组合，4：必须为大小写字母，数字，特殊字组合", example = "0")
    private Integer pwdComplex;
}