package com.virtual.cloud.om.sdk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台配置实体
 */
@Data
@TableName("platform_config")
public class PlatformConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 平台类型: WORKSPACE/CAS/UIS
     */
    private String platformType;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 平台描述
     */
    private String description;

    /**
     * 状态: NORMAL/ABNORMAL/DISABLED
     */
    private String status;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
