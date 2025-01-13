package com.virtual.cloud.om.dto;

import lombok.Data;

@Data
public class TestSshConnectResultDTO {
    private String resourceId;
    private Integer hostId;
    private String uuid;
    /**
     * 连接状态
     * 0-测试连接失败
     * 1-测试连接成功（允许远程命令行）
     * 2-测试连接成功（不允许远程命令行）
     * 3-测试连接错误次数达3次，等待一分钟
     */
    private Integer state;
    private String watcherCode;
    private String watcherIp;
}
