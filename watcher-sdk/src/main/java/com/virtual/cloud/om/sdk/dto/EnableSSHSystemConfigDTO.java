package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/2 16:08
 */
@Data
public class EnableSSHSystemConfigDTO implements Serializable {

    private static final long serialVersionUID = -3018196798035846847L;
    private Boolean sshEnable;
}
