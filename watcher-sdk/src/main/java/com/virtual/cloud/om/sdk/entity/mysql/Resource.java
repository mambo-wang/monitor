package com.virtual.cloud.om.sdk.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源实体 - 映射 resource 表
 */
@Data
@TableName("resource")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String resourceName;

    private String platform;

    private String ipAddress;

    private Integer port;

    private String protocol;

    private String authType;

    private String ac;

    private String ci;

    private String serverUsername;

    private String serverPassword;

    private Integer serverPort;

    private Integer active;

    private Integer usable;

    private Integer remote;

    private LocalDateTime endTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
