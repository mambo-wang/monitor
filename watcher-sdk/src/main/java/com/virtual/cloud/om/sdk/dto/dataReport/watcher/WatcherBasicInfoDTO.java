package com.virtual.cloud.om.sdk.dto.dataReport.watcher;

import lombok.Data;

@Data
public class WatcherBasicInfoDTO {
    private Integer master;
    private String ip;
    private String vip;
    private String version;
    private Integer status;
    private Integer agentStatus;
    private Integer nginxStatus;
    private Integer kafkaStatus;
    private Integer mongodbStatus;
    private Integer zookeeperStatus;
    private Integer keepalivedStatus;
}
