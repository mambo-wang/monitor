package com.virtual.cloud.om.sdk.dto.inspect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/25 16:53
 */
@Data
public class Slave implements Serializable {
    private static final long serialVersionUID = -261060652992436097L;
    private String name;
    private String ip;
    private String status;
    private String normal;
}
