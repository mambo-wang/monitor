package com.virtual.cloud.om.sdk.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 资源实体 - MySQL版本
 */
@Data
@TableName("resource_entity")
public class ResourceEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.INPUT)
    private String id;
    
    private String ipAddress;
    
    private Integer port;
    
    private String protocol;
    
    private String authType;
    
    private String ac;
    
    private String ci;
    
    private String platform;
    
    private String serverUsername;
    
    private String serverPassword;
    
    private Integer serverPort;
    
    private Integer active;
    
    private String createTime;
    
    private String updateTime;
}
