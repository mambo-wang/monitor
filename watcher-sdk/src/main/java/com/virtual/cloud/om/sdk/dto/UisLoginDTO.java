package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/6 15:53
 */
@Data
public class UisLoginDTO implements Serializable {
    private static final long serialVersionUID = 5947815833931648792L;

    private Boolean encrypt;
    private String loginType;
    private String name;
    private String password;
}
