package com.virtual.cloud.om.sdk.dto.dataReport.cas;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

/**
 * Restful Web Services 接口请求/返回的虚拟机实体类,包含storage。
 */
@Data
@ToString
@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainDetailInfoDTO implements Serializable {

    private static final long serialVersionUID = 7630696172783821368L;

    public static final int STATUS_SHUTOFF = 3;
    public static final int STATUS_UNKNOWN = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_PAUSED = 4;

    /**
     * 虚拟机ID。
     */
    @ApiModelProperty(value = "虚拟机ID")
    private Long id = null;

    /**
     * 物理机ID。 *
     */
    @ApiModelProperty(value = "物理机ID")
    private Long hostId;

    /**
     * 集群ID。
     */
    @ApiModelProperty(value = "集群ID")
    private Long clusterId = null;

    /**
     * 主机池ID。
     */
    @ApiModelProperty(value = "主机池ID")
    private Long hostPoolId;

    /**
     * 虚拟机名称。
     */
    @ApiModelProperty(value = "虚拟机名称")
    private String name;

    /**
     * 虚拟机显示名称。 *
     */
    @ApiModelProperty(value = "虚拟机显示名称")
    private String title;

    /**
     * 虚拟机内存。兆。 *
     */
    @ApiModelProperty(value = "虚拟机内存")
    private Long memory;

    /**
     * 虚拟机虚拟CPU个数（CPU个数 * CPU核数）。 *
     */
    @ApiModelProperty(value = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    /**
     * 虚拟机状态 *
     */
    @ApiModelProperty(value = "虚拟机状态。")
    private Integer status;

    /**
     * 虚拟机UUID。 *
     */
    @ApiModelProperty(value = "虚拟机UUID")
    private String uuid;

    /**
     * 虚拟机安装的操作系统。取值：0:Windows;1:Linux。 *
     */
    @ApiModelProperty(value = "虚拟机安装的操作系统。取值：0:Windows;1:Linux")
    private Integer system;

    @ApiModelProperty(value = "虚拟机系统位数。取值： x86_64 x86")
    private String osBit;

    /**
     * 引导设备：1 disk 2 cdrom。 *
     */
    @ApiModelProperty(value = "引导设备：1 disk 2 cdrom")
    private Integer bootingDevice;

    /**
     * 是否自动启动：0:不自动启动1:自动启动。 *
     */
    @ApiModelProperty(value = "是否自动启动：0:不自动启动1:自动启动")
    private Integer autoBooting;

    /**
     * 虚拟机网络。
     */
    @XmlElement(name = "network")
    @ApiModelProperty(value = "虚拟机网络列表")
    private List<DomainNetworkDTO> network = null;

    /**
     * 虚拟机存储。
     */
    @XmlElement(name = "storage")
    @JsonProperty("storage")
    @ApiModelProperty(value = "虚拟机存储列表")
    private List<DomainStorageDTO> storage = null;

    @XmlElement(name = "video")
    @JsonProperty("video")
    @ApiModelProperty(value = "显卡")
    private List<DomainVideoDTO> video = null;

    @XmlElement(name = "graphics")
    @JsonProperty("graphics")
    @ApiModelProperty(value = "图形化设备，如 \"sdl\", \"vnc\", \"rdp\", \"desktop\"")
    private List<DomainGraphicsDTO> graphics = null;

    /**
     * 虚拟机创建日期。
     */
    @ApiModelProperty(value = "虚拟机创建日期")
    private Date createDate;

    /**
     * 虚拟机所属标志 0:不属于用户以及用户组 1:属于用户 2:属于用户组。
     */
    @ApiModelProperty(value = "虚拟机所属标志 0:不属于用户以及用户组 1:属于用户 2:属于用户组")
    private Integer flag = 1;

    /**
     * 虚拟机类型 0:CAS虚拟机 1:VMware虚拟机。
     */
    @ApiModelProperty(value = "虚拟机类型 0:CAS虚拟机 1:VMware虚拟机")
    private Integer type = 0;

    /**
     * 是否快速部署过。
     */
    @ApiModelProperty(value = "是否快速部署过")
    private Boolean deployed = false;

    /**
     * VNC 端口 自动配置0：自动1：手动。 *
     */
    @ApiModelProperty(value = "VNC 端口 自动配置0：自动1：手动")
    private Integer auto;

    /**
     * 虚拟机是否启用保护模式。 1：启用，0：不启用 *
     */
    @ApiModelProperty(value = "虚拟机是否启用保护模式。 1：启用，0或null：不启用")
    private Integer protectModel;

    /**
     * 对应UIS数据库ENABLE值
     */
    @ApiModelProperty(hidden = true)
    private Integer enable;

    /**
     * 主机状态 *
     */
    @ApiModelProperty(value = "主机状态。")
    private Integer hoststatus;

    /**
     * 主机所在集群是否启用HA
     */
    @ApiModelProperty(value = "主机所在集群是否启用HA")
    private Integer hostHaEnable;

    /**
     * castool状态 0:未运行 1:运行
     */
    @ApiModelProperty(value = "castool状态 0:未运行 1:运行")
    private Integer castoolsStatus;

    /**
     * 虚拟机是否启用防病毒配置。1：启用，0为不启用。
     */
    @ApiModelProperty(value = "虚拟机是否启用防病毒配置。1：启用，0为不启用")
    private int antivirusEnable = 0;

    @ApiModelProperty(value = "虚拟机使用时间")
    private int uptime = 0;

    /*虚拟机分类 默认为1 1 普通虚拟机  101~200由VDI自定义（101，VDI镜像 ； 102 --110 预留 ； 111开始为IDV不同终端类型对应镜像）*/
    @ApiModelProperty(value = "虚拟机分类 默认为1 1 普通虚拟机")
    private Integer vmType = 1;

    /** 显卡类型：Linux系统默认使用cirrus；Windows系统默认使用vga；VDI使用qxl  **/
    @ApiModelProperty(value = "显卡类型：Linux系统默认使用cirrus；Windows系统默认使用vga；VDI使用qxl")
    private String videoType;
}

