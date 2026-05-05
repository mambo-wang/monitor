package com.virtual.cloud.om.sdk.constant;

/**
 * CAS日志收集的种类
 * */
public enum OperationLogTypeEnum {
    vm(10, "虚拟机动作")
    ;
    public final Integer type;
    private final String desc;

    OperationLogTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() { return type; }
    public String getDesc() { return desc; }
}
