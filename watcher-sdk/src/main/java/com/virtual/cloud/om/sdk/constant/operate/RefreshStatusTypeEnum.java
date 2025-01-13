package com.virtual.cloud.om.sdk.constant.operate;

import com.virtual.cloud.om.sdk.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum RefreshStatusTypeEnum {
    reportServerStatus(1,"服务器"),
    reportDomainStatus(2,"虚拟机"),
    ;

    public final Integer type;
    public final String title;

    public static RefreshStatusTypeEnum getByType(Integer type){
        Optional<RefreshStatusTypeEnum> first = Arrays.stream(values()).filter(e -> e.type.equals(type)).findFirst();
        if(!first.isPresent()){
            //TODO errorcode
            throw new AppException(1);
        }
        return first.get();
    }
}
