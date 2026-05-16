package com.virtual.cloud.om.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tool_share_file")
public class ToolShareFile implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileName;
    private String toolName;
    private String toolDesc;
    private String filePath;
    private Long fileSize;
    private Long downloadCount;
    private Long folderId;
    private Long createUserId;
    private LocalDateTime createTime;
}