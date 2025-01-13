package com.virtual.cloud.om.sdk.config.token.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by l19767 on 2019/12/10.
 */
@NoArgsConstructor
@Data
@ToString
public class WsTokenResult implements Serializable {
    private static final long serialVersionUID = 7341843330854961944L;


    @JsonProperty("loginName")
    private String loginName;
    @JsonProperty("pwd")
    private Object pwd;
    @JsonProperty("oldPwd")
    private Object oldPwd;
    @JsonProperty("id")
    private Object id;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("credentialNumber")
    private Object credentialNumber;
    @JsonProperty("email")
    private Object email;
    @JsonProperty("idleTimeout")
    private Integer idleTimeout;
    @JsonProperty("online")
    private Boolean online;
    @JsonProperty("loginTime")
    private Long loginTime;
    @JsonProperty("loginIp")
    private String loginIp;
    @JsonProperty("sessionId")
    private String sessionId;
    @JsonProperty("loginFailErrorCode")
    private Integer loginFailErrorCode;
    @JsonProperty("loginFailMessage")
    private Object loginFailMessage;
    @JsonProperty("additionalInfo")
    private AdditionalInfoDTO additionalInfo;
    @JsonProperty("permissions")
    private List<String> permissions;
    @JsonProperty("operatorGroupId")
    private Integer operatorGroupId;
    @JsonProperty("operatorGroupName")
    private String operatorGroupName;
    @JsonProperty("operatorGroupCode")
    private Object operatorGroupCode;
    @JsonProperty("operatorGroupMode")
    private Object operatorGroupMode;
    @JsonProperty("flag")
    private Object flag;
    @JsonProperty("applySRM")
    private Boolean applySRM;
    @JsonProperty("version")
    private Integer version;
    @JsonProperty("forward")
    private Object forward;
    @JsonProperty("operSkin")
    private Object operSkin;
    @JsonProperty("initComplete")
    private Object initComplete;
    @JsonProperty("selectLanguage")
    private Object selectLanguage;
    @JsonProperty("uuid")
    private Object uuid;
    @JsonProperty("pwdExpireDay")
    private Integer pwdExpireDay;
    @JsonProperty("authType")
    private Integer authType;
    @JsonProperty("code")
    private Object code;
    @JsonProperty("tmpLicenseExpireDay")
    private Integer tmpLicenseExpireDay;
    @JsonProperty("lastHeartBeatTime")
    private Long lastHeartBeatTime;
    @JsonProperty("token")
    private String token;
    @JsonProperty("modifyPwd")
    private Boolean modifyPwd;
    @JsonProperty("ssoLogin")
    private Boolean ssoLogin;

    @NoArgsConstructor
    @Data
    public static class AdditionalInfoDTO {
    }
}
