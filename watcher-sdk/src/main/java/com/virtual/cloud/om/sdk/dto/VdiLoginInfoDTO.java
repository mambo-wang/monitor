package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/9 16:45
 */
@Data
public class VdiLoginInfoDTO implements Serializable {

        private static final long serialVersionUID = -4416218332241209719L;
        private String loginName = null;
        private String pwd = null;


}
