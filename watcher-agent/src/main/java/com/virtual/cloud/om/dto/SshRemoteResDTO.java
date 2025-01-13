package com.virtual.cloud.om.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/2 17:08
 */
@Data
public class SshRemoteResDTO implements Serializable {

    private static final long serialVersionUID = 3344806701941785936L;

    private String uuid;
    private Integer result;
    private String message;
}
