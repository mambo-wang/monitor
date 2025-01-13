package com.virtual.cloud.om.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "NetworkConfig")
public class NetworkConfig {
    @Id
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
