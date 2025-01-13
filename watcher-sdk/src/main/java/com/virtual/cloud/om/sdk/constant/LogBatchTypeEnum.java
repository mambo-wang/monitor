package com.virtual.cloud.om.sdk.constant;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@AllArgsConstructor
public enum LogBatchTypeEnum {
    domain,
    terminal,
    host,
    agent,
    nginx,
    kafka,
    mongodb,
    zookeeper,
    keepalived
    ;
    
    public static List<LogBatchTypeEnum> getWatcherLogEnumList(){
        return new ArrayList<>(Arrays.asList(
                agent,
                nginx,
                kafka,
                mongodb,
                zookeeper,
                keepalived
        ));
    }


}
