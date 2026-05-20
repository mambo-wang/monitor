package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "rsShareFileSystem")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShareFileDTO implements Serializable {

    @Schema(description = "共享文件系统ID")
    private long id;

    @Schema(description = "共享文件系统名称")
    private String name;

    @Schema(description = "共享文件系统类型")
    private String typeStr;

    @Schema(description = "mount目录")
    private String path;

    @Schema(description = "总容量")
    private Long maxSize;

    @Schema(description = "可用容量")
    private Long remainSize;

    @Schema(description = "已用率")
    private Double usAge;

    @Schema(description = "已分配容量")
    private Double allocation;

    @Schema(description = "虚拟机自动部署控制，0：不允许，1：允许")
    private Integer capacityControl;

}
