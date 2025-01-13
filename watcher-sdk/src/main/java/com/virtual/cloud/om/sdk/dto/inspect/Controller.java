package com.virtual.cloud.om.sdk.dto.inspect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/25 17:00
 */
@Data
public class Controller implements Serializable {
    private static final long serialVersionUID = 1120814930481365884L;

    private String type;
    private String name;
    private String ip;
    private String status;
    private String ip1;
    private String ip2;
}
