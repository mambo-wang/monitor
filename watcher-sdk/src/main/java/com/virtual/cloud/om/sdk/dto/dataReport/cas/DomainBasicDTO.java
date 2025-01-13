package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class DomainBasicDTO implements Serializable {

    private static final long serialVersionUID = 7630696172783821368L;

    /**
     * 虚拟机ID。
     */
    @ApiModelProperty(value = "虚拟机ID")
    private Long id = null;

    /**
     * 虚拟机UUID。 *
     */
    @ApiModelProperty(value = "虚拟机UUID")
    private String uuid;

    /**
     * 集群ID。
     */
    @ApiModelProperty(value = "集群ID")
    private Long clusterId = null;

    /**
     * 虚拟机名称。
     */
    @ApiModelProperty(value = "虚拟机名称")
    private String domainName;

    /**
     * 虚拟机显示名称。 *
     */
    @ApiModelProperty(value = "虚拟机显示名称")
    private String title;

    /**
     * 虚拟机描述。 *
     */
    @ApiModelProperty(value = "虚拟机描述")
    private String description;

    /**
     * 物理机ID。 *
     */
    @ApiModelProperty(value = "物理机ID")
    private Long hostId;

    /**
     * castool状态 0:未运行 1:运行
     */
    @ApiModelProperty(value = "castool状态 0:未运行 1:运行")
    private Integer casToolStatus;

    /**
     * castool版本。 *
     */
    @ApiModelProperty(value = "castool版本")
    private String casToolVersion;

    /**
     * 虚拟机安装的操作系统。取值：0:Windows;1:Linux。 *
     */
    @ApiModelProperty(value = "虚拟机安装的操作系统。取值：0:Windows;1:Linux")
    private Integer system;


    /**
     * 虚拟机状态 *
     */
    @ApiModelProperty(value = "虚拟机状态。")
    private Integer status;


    /**
     * 虚拟机安装的操作系统描述。 *
     */
    @ApiModelProperty(value = "操作系统描述")
    private String osVersion;

    @ApiModelProperty(value = "虚拟机使用时间")
    private int uptime = 0;

    /**
     * 虚拟机创建日期。
     */
    @ApiModelProperty(value = "虚拟机创建日期")
    private Date createDate;

    @ApiModelProperty(value = "虚拟机是否允许自动迁移")
    private Integer autoMigrate;

    /**
     * 虚拟机是否启用保护模式。 1：启用，0：不启用 *
     */
    @ApiModelProperty(value = "虚拟机是否启用保护模式。 1：启用，0或null：不启用")
    private Integer protectModel;

    @ApiModelProperty(value = "是否开启vnc代理")
    private Integer enableVncProxy;

    /**
     * 虚拟机虚拟CPU个数（CPU个数 * CPU核数）。 *
     */
    @ApiModelProperty(value = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    @ApiModelProperty(value="虚拟机cpu颗数",example="1")
    private Integer cpuSocket;

    @ApiModelProperty(value="cpu核数",example="1")
    private Integer cpuCore;

    /**
     * 虚拟机内存。兆。 *
     */
    @ApiModelProperty(value = "虚拟机内存")
    private Long memory;

    @ApiModelProperty(value = " vnc端口")
    private Integer vncPort;

    @ApiModelProperty(value = "显示类型")
    private String displayType;

    /**
     * 虚拟机存储。
     */
    @XmlElement(name = "storage")
    @JsonProperty("storage")
    @ApiModelProperty(value = "虚拟机存储列表")
    private List<DomainStorageDTO> storage = null;

    /**
     * 虚拟机网络。
     */
    @XmlElement(name = "network")
    @ApiModelProperty(value = "虚拟机网络列表")
    private List<DomainNetworkDTO> network = null;
}
