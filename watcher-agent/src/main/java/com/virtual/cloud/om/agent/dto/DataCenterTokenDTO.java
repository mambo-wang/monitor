package com.virtual.cloud.om.agent.dto;

import lombok.Data;

@Data
public class DataCenterTokenDTO {
    private String id;

    /**ip*/
    private String ip;

    /**token*/
    private String token;

    /**comCode + orgCode*/
    private String tenantIdEncode;

    private Long timeout;
    private String dataCenterKey;

    /**机构名称*/
    private String comName;

    /**机构代码*/
    private String comCode;

    /**组织名称*/
    private String orgName;

    /**组织代码*/
    private String orgCode;

    /**云上token*/
    private String cloudToken;

    private Long cloudTimeout;
}
