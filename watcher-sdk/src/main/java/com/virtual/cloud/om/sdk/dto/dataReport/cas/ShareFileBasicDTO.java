package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ShareFileBasicDTO {

    @ApiModelProperty(value = "共享文件系统ID")
    private long id;

    @ApiModelProperty(value = "共享文件系统名称")
    private String name;

    @ApiModelProperty(value = "共享文件系统显示名称")
    private String title;

    @ApiModelProperty(value = "共享文件系统类型")
    private String type;

    @ApiModelProperty(value = "mount目录")
    private String path;

    @ApiModelProperty(value = "总容量")
    private Long totalSize;

    @ApiModelProperty(value = "可用容量")
    private Long freeSize;

    @ApiModelProperty(value = "已分配容量")
    private Double allocation;

}
