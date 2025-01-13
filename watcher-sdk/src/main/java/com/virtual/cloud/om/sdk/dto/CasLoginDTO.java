package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/5 17:51
 */
@Data
public class CasLoginDTO implements Serializable {
    private static final long serialVersionUID = -4727686536021807537L;
    private String name;
    private String password;
    //是否加密
    private Boolean encrypt;
    // cn
    private String lang;

    private Long t;
    private  Boolean isForce;

}
