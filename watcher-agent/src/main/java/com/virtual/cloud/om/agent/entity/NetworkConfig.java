package com.virtual.cloud.om.agent.entity;

import lombok.Builder;
import lombok.Data;



@Data
@Builder

public class NetworkConfig {
    
    private String nodeName;
    private String innerName;
    private String innerIp;
    private String innerAllocation;
    private String innerMask;
    private String innerGateway;
    private String outerName;
    private String outerIp;
    private String outerAllocation;
    private String outerMask;
    private String outerGateway;
    private String outerWifi;
    private String outerWifiPwd;
    private String dns1;
    private String dns2;
    private Long createTime;
    private Long updateTime;
}
