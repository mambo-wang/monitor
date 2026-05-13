package com.virtual.cloud.om.agent.dto;

import lombok.Data;

/**
 * 资源项 DTO
 * 用于大屏概览功能，传输资源（桌面池、终端、集群、主机、虚拟机）信息
 */
@Data
public class ResourceItemDTO {
    
    /**
     * 资源 ID
     */
    private Long id;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源类型：DESKTOP_POOL, TERMINAL, CLUSTER, HOST, VIRTUAL_MACHINE
     */
    private String type;
    
    /**
     * 资源状态：NORMAL, ABNORMAL, UNKNOWN
     */
    private String status;
    
    /**
     * 父资源 ID
     */
    private Long parentId;
    
    /**
     * 资源描述
     */
    private String description;
}