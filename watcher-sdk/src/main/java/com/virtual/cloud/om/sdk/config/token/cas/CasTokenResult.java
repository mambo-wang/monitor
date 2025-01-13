package com.virtual.cloud.om.sdk.config.token.cas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
@ToString
public class CasTokenResult implements Serializable {
    private static final long serialVersionUID = 586805828652414127L;
    @JsonProperty("loginName")
    private String loginName;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("idleTimeout")
    private Integer idleTimeout;
    @JsonProperty("online")
    private Boolean online;
    @JsonProperty("loginTime")
    private String loginTime;
    @JsonProperty("loginIp")
    private String loginIp;
    @JsonProperty("sessionId")
    private String sessionId;
    @JsonProperty("loginFailErrorCode")
    private Integer loginFailErrorCode;
    @JsonProperty("additionalInfo")
    private String additionalInfo;
    @JsonProperty("permissions")
    private List<String> permissions;
    @JsonProperty("operatorGroupId")
    private Integer operatorGroupId;
    @JsonProperty("operatorGroupName")
    private String operatorGroupName;
    @JsonProperty("operatorGroupCode")
    private String operatorGroupCode;
    @JsonProperty("operatorGroupMode")
    private Integer operatorGroupMode;
    @JsonProperty("flag")
    private Integer flag;
    @JsonProperty("applySRM")
    private Boolean applySRM;
    @JsonProperty("version")
    private Integer version;
    @JsonProperty("isSsoLogin")
    private Boolean isSsoLogin;
    @JsonProperty("loginFailMessage")
    private String loginFailMessage;

/*    @NoArgsConstructor
    @Data
    public class AdditionalInfoDTO {
        @JsonProperty("User-Agent")
        String userAgent;
    }*/
}
