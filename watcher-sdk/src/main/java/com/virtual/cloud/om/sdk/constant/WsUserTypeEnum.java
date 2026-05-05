package com.virtual.cloud.om.sdk.constant;

import lombok.Getter;

@Getter
public enum WsUserTypeEnum {
    local_user(0, "本地用户"),
    domain_user(1, "域用户"),
    ldap_user(2, "LDAP用户")
    ;

    public final Integer type;
    public final String desc;

    WsUserTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
