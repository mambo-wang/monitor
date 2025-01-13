package com.virtual.cloud.om.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/10 10:55
 * 保存认证后token
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "DataCenterToken")//相当于数据库里的表名
public class DataCenterToken {

    @Id
    private String id;

    /**ip*/
    private String ip;

    /**token*/
    private String token;

    private String route;

    private Long timeout;
    private String secretKey;

    /**机构名称*/
    private String comName;

    /**机构代码*/
    private String comCode;

    /**组织名称*/
    private String orgName;

    /**组织代码*/
    private String orgCode;

    private String port;

    /**云上token*/
    private String cloudToken;

    private Long cloudTimeout;

    private Integer datacenterType;
}
