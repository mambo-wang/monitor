package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/9/16 11:27
 */
@Data
public class VdisshCheckResult implements Serializable {

    private static final long serialVersionUID = 5159026397261694330L;
    private String loginFailErrorCode;
    private String loginFailMessage;
    private List<String> permissions;
}
