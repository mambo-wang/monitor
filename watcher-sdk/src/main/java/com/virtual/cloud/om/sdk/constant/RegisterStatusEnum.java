package com.virtual.cloud.om.sdk.constant;

/**
 * 注册申请状态枚举
 */
public enum RegisterStatusEnum {

    PENDING("pending", "待审批"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已拒绝");

    private final String code;
    private final String desc;

    RegisterStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RegisterStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (RegisterStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
