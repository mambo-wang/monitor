package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

@Data
public class CasHostInfoDTO {
    private static final long serialVersionUID = 1L;
    private Integer childNum;
    private String ip;
    private Object pw;
    private String name;
    private String id;
    private Integer clusterId;
    private Integer haEnable;
    private Integer type;
    private String user;
    private Integer hostPoolId;
    private Integer status;
}
