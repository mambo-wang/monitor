package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Schema
public class DomainBasicDTO implements Serializable {

    private static final long serialVersionUID = 7630696172783821368L;

    /**
     * 虚拟机ID。
     */
    @Schema(description = "虚拟机ID")
    private Long id = null;

    /**
     * 虚拟机UUID。 *
     */
    @Schema(description = "虚拟机UUID")
    private String uuid;

    /**
     * 集群ID。
     */
    @Schema(description = "集群ID")
    private Long clusterId = null;

    /**
     * 虚拟机名称。
     */
    @Schema(description = "虚拟机名称")
    private String domainName;

    /**
     * 虚拟机显示名称。 *
     */
    @Schema(description = "虚拟机显示名称")
    private String title;

    /**
     * 虚拟机描述。 *
     */
    @Schema(description = "虚拟机描述")
    private String description;

    /**
     * 物理机ID。 *
     */
    @Schema(description = "物理机ID")
    private Long hostId;

    /**
     * castool状态 0:未运行 1:运行
     */
    @Schema(description = "castool状态 0:未运行 1:运行")
    private Integer casToolStatus;

    /**
     * castool版本。 *
     */
    @Schema(description = "castool版本")
    private String casToolVersion;

    /**
     * 虚拟机安装的操作系统。取值：0:Windows;1:Linux。 *
     */
    @Schema(description = "虚拟机安装的操作系统。取值：0:Windows;1:Linux")
    private Integer system;


    /**
     * 虚拟机状态 *
     */
    @Schema(description = "虚拟机状态。")
    private Integer status;


    /**
     * 虚拟机安装的操作系统描述。 *
     */
    @Schema(description = "操作系统描述")
    private String osVersion;

    @Schema(description = "虚拟机使用时间")
    private int uptime = 0;

    /**
     * 虚拟机创建日期。
     */
    @Schema(description = "虚拟机创建日期")
    private Date createDate;

    @Schema(description = "虚拟机是否允许自动迁移")
    private Integer autoMigrate;

    /**
     * 虚拟机是否启用保护模式。 1：启用，0：不启用 *
     */
    @Schema(description = "虚拟机是否启用保护模式。 1：启用，0或null：不启用")
    private Integer protectModel;

    @Schema(description = "是否开启vnc代理")
    private Integer enableVncProxy;

    /**
     * 虚拟机虚拟CPU个数（CPU个数 * CPU核数）。 *
     */
    @Schema(description = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    @Schema(description="虚拟机cpu颗数",example="1")
    private Integer cpuSocket;

    @Schema(description="cpu核数",example="1")
    private Integer cpuCore;

    /**
     * 虚拟机内存。兆。 *
     */
    @Schema(description = "虚拟机内存")
    private Long memory;

    @Schema(description = " vnc端口")
    private Integer vncPort;

    @Schema(description = "显示类型")
    private String displayType;

    /**
     * 虚拟机存储。
     */
    @XmlElement(name = "storage")
    @JsonProperty("storage")
    @Schema(description = "虚拟机存储列表")
    private List<DomainStorageDTO> storage = null;

    /**
     * 虚拟机网络。
     */
    @XmlElement(name = "network")
    @Schema(description = "虚拟机网络列表")
    private List<DomainNetworkDTO> network = null;
}
