package com.virtual.cloud.om.sdk.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
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
