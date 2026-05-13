package com.virtual.cloud.om.agent.dto;

import lombok.Data;

/**
 * 管理平台 DTO
 * 用于大屏概览功能，传输管理平台（Workspace/CAS/UIS）信息
 */
@Data
public class PlatformDTO {
    
    /**
     * 平台 ID
     */
    private Long id;
    
    /**
     * 平台名称
     */
    private String name;
    
    /**
     * 平台类型：WORKSPACE, CAS, UIS
     */
    private String type;
    
    /**
     * 平台描述
     */
    private String description;
    
    /**
     * 平台状态：NORMAL, ABNORMAL, UNKNOWN
     */
    private String status;
}