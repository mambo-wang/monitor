package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/12 17:47
 */
@Data
public class TokenAndWatcherCodeDTO {

    /**ip*/
    private String ip;

    private String token;

    private Long timeout;

    private String route;

    private byte[] secretKey;

    private String watcherCode;
    /**机构代码*/
    private String comCode;

    /**组织代码*/
    private String orgCode;

    private String port;

    /**云上token*/
    private String cloudToken;

    private Long cloudTimeout;

    private Integer datacenterType;
}