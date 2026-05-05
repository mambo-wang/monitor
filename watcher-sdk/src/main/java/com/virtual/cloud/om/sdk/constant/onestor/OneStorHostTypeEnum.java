package com.virtual.cloud.om.sdk.constant.onestor;

/**
 * Onestor 主机类别
 * */
public enum OneStorHostTypeEnum {
    handy(""),
    stor("存储节点"),
    mon(""),
    rgw(""),
    nas(""),
    mds("")
    ;

    private final String desc;

    OneStorHostTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() { return desc; }
}
