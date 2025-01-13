package com.virtual.cloud.om.sdk.dto.inspect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/25 16:43
 */
@Data
public class Master implements Serializable {
    private static final long serialVersionUID = -8552998840159213265L;
    private String name;
    private String ip;
    private String status;
    private String normal;
}
