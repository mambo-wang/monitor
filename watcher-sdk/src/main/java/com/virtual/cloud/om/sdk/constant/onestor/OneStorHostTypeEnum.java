package com.virtual.cloud.om.sdk.constant.onestor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Onestor 主机类别
 * */
@AllArgsConstructor
@Getter
public enum OneStorHostTypeEnum {
    handy(""),
    stor("存储节点"),
    mon(""),
    rgw(""),
    nas(""),
    mds("")
    ;
    private final String desc;
}
