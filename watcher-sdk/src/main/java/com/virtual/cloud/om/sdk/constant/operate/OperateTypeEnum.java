package com.virtual.cloud.om.sdk.constant.operate;

import com.virtual.cloud.om.sdk.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@SuppressWarnings("all")
public enum OperateTypeEnum {
    start(1, "开机"),
    stop(2, "安全关机"),
    shutdown(3, "关闭电源"),
    restart(4, "重启"),
    intoMaintain(5, "维护"),
    exitMaintain(6, "退出维护"),
    pause(7, "暂停"),
    refresh(8, "刷新"),
    wake(9, "唤醒"),
    ;

    public final Integer type;
    public final String title;

    public static OperateTypeEnum getByType(Integer type){
        Optional<OperateTypeEnum> first = Arrays.stream(values()).filter(e -> e.type.equals(type)).findFirst();
        if(!first.isPresent()){
            //TODO errorcode
            throw new AppException(1);
        }
        return first.get();
    }
}
