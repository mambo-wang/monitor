package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ShareFileBasicDTO {

    @Schema(description = "共享文件系统ID")
    private long id;

    @Schema(description = "共享文件系统名称")
    private String name;

    @Schema(description = "共享文件系统显示名称")
    private String title;

    @Schema(description = "共享文件系统类型")
    private String type;

    @Schema(description = "mount目录")
    private String path;

    @Schema(description = "总容量")
    private Long totalSize;

    @Schema(description = "可用容量")
    private Long freeSize;

    @Schema(description = "已分配容量")
    private Double allocation;

}
