package com.virtual.cloud.om.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tool_share_folder")
public class ToolShareFolder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private Long createUserId;
    private LocalDateTime createTime;
}