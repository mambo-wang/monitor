package com.virtual.cloud.om.sdk.entity.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent唯一码表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("agent_unique_code")
public class AgentUniqueCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * Agent唯一标识
     */
    private String uid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
