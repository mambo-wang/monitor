package com.virtual.cloud.om.sdk.constant.operate;

import com.virtual.cloud.om.sdk.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum ObjectTypeEnum {
    host(1,"服务器"),
    vm(2,"虚拟机"),
    desktoppool(3,"桌面池"),
    terminal(4,"终端"),
    desktop(5,"桌面"),
    storagePool(6,"存储池"),
    shareStorage(7,"共享存储"),
    watcher(8,"采集端"),
    ;

    public final Integer type;
    public final String title;

    public static ObjectTypeEnum getByType(Integer type){
        Optional<ObjectTypeEnum> first = Arrays.stream(values()).filter(e -> e.type.equals(type)).findFirst();
        if(!first.isPresent()){
            //TODO errorcode
            throw new AppException(1);
        }
        return first.get();
    }
}
