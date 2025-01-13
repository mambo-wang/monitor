package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "rsShareFileSystem")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShareFileDTO implements Serializable {

    @ApiModelProperty(value = "共享文件系统ID")
    private long id;

    @ApiModelProperty(value = "共享文件系统名称")
    private String name;

    @ApiModelProperty(value = "共享文件系统类型")
    private String typeStr;

    @ApiModelProperty(value = "mount目录")
    private String path;

    @ApiModelProperty(value = "总容量")
    private Long maxSize;

    @ApiModelProperty(value = "可用容量")
    private Long remainSize;

    @ApiModelProperty(value = "已用率")
    private Double usAge;

    @ApiModelProperty(value = "已分配容量")
    private Double allocation;

    @ApiModelProperty(value = "虚拟机自动部署控制，0：不允许，1：允许")
    private Integer capacityControl;

}
