package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by z13465 on 2019/10/15
 */
@Data
@Schema
@ToString
@EqualsAndHashCode
@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class

ImageResponseDTO {

    private static final long serialVersionUID = -4471848386315891114L;

    @Schema(description = "镜像id")
    private Long id;

    @Schema(description = "虚拟机id")
    private Long domainId;

    @Schema(description = "虚拟机uuid")
    private String domainUuid;

    @Schema(description = "虚拟机名称")
    private String domainName;

    @Schema(description = "计算机名")
    private String title;

    @Schema(description = "集群 id")
    private Long clusterId;

    @Schema(description = "主机id")
    private Long hostId;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "内存大小 MB")
    private Integer memory;

    @Schema(description = "分配的内存大小")
    private Double memoryInit;

    @Schema(description = "cpu")
    private String cpu;

    @Schema(description = "vdi模板 id")
    private Long templateId;

    @Schema(description = "vdi模板 uuid")
    private String templateUuid;

    @Schema(description = "存储大小GB")
    private Long storage;

    @Schema(description = "0:Windows;1:Linux ")
    private Integer system;

    @Schema(description = "系统")
    private String osVersion;

    @Schema(description = "模板存储位置")
    private String templetStoragePath;

    @Schema(description = "当前状态，1为虚拟机；2为CAS对应的template; 3为IDV所使用的镜像文件")
    private Integer imageStatus;

    @Schema(description = "虚拟机镜像文件类型")
    private String terminalType;

    @Schema(hidden = true)
    private Short addFrom;

    @Schema(description = "镜像类型，VDI为0，IDV为1", example = "1")
    private Integer templateType;

    @Schema(description = "镜像创建时间")
    private Long createTime;

    @Schema(description = "虚拟机ip")
    private List<String> ips;

    @Schema(description = "快照名")
    private List<String> snapShotName;

    @Schema(description = "配置名")
    private String flavorName;

    @Schema(description = "虚拟机状态")
    private Integer domainStatus;

    @Schema(description = "主机名")
    private String hostName;

    @Schema(description = "虚拟机网络")
    private String network;

    @Schema(description = "存储池路径")
    private String storagePoolPath;

    @Schema(description = "父镜像name")
    private String parentImageName;

    @Schema(description ="是否正在创建。0：否 ，1：是")

    private Integer isCreating;


    @Schema(description ="是否在生成模板 0：否 ，1：转换生成， 2:克隆生成")
    private Integer isGenerating;

    @Schema(description ="voi vhd文件格式，1：薄存储，2：厚存储" )
    private Integer voiDiskMode;

    @Schema(description ="发布状态,1：立即发布，2：灰度发布，默认为1")
    private Integer publishStatus;

    @Schema(description ="版本号，管理员未输入为镜像虚拟机名称+日期，否则镜像虚拟机名称+管理员输入")
    private String version;

    @Schema(description ="镜像发布时间")
    private Long publishTime;


    @Schema(description ="镜像文件类型")
    private Integer imageFileType;
}
