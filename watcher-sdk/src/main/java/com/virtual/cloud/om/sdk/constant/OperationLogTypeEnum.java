package com.virtual.cloud.om.sdk.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CAS日志收集的种类
 * */
@AllArgsConstructor
@Getter
public enum OperationLogTypeEnum {
    vm(10, "虚拟机动作")
    ;
    public final Integer type;
    private final String desc;
}
