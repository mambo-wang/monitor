package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@ToString
@XmlRootElement(name = "domainSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainDetailDTO {

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

    @ApiModelProperty(value = "虚拟机显示名称")
    private String title;

    @ApiModelProperty(value = "虚拟机名称")
    private String domainName;

    private Boolean isEdit;

    @ApiModelProperty(value = "主机名称")
    private String hostName;

    @ApiModelProperty(value="主机ip",example="1")
    private String hostIp;

    @ApiModelProperty(value = "虚拟机状态。取值： 0:模板 1:未知 2:运行 3:关闭 4 暂停。")
    private Integer status;

    @ApiModelProperty(value="操作系统版本")
    private String osVersion;

    @ApiModelProperty(value = "虚拟机安装的操作系统。取值：0:Windows;1:Linux,2:BSD")
    private Integer system;

    @ApiModelProperty(value = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    @ApiModelProperty(value="虚拟机cpu颗数",example="1")
    private Integer cpuSocket;

    @ApiModelProperty(value="cpu核数",example="1")
    private Integer cpuCore;

    @ApiModelProperty(value = "虚拟机内存")
    private String memory;

    @ApiModelProperty(value = "虚拟机CPU利用率")
    private Double cpuRate;

    @ApiModelProperty(value = "虚拟机内存利用率")
    private Double memoryRate;

    @ApiModelProperty(value="虚拟机存储 带单位 用于前台显示概要信息",example="1")
    private String storage;

    @ApiModelProperty(value = "castools状态,用于页面显示 0:未运行 1:运行")
    private String castools;

    @ApiModelProperty(value = "castools是否运行 0:未运行 1:运行")
    private Boolean castoolRunning;

    @ApiModelProperty(value = "castools状态 0:未运行 1:运行")
    private Integer castoolStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "虚拟机运行时间 单位分钟，-1表示未知")
    private int uptime = 0;

    @ApiModelProperty(value = "最后关机时间 单位分钟，-1表示未知")
    private int lastOffTime = 0;

    @ApiModelProperty(value = "虚拟机UUID")
    private String uuid;

    @ApiModelProperty(value = "虚拟机系统位数。取值： x86_64 x86")
    private String osBit;

    @ApiModelProperty(value = "显示类型")
    private String displayType;

    @ApiModelProperty(value = "自动配置")
    private String autoConfig;

    @ApiModelProperty(value = " vnc端口")
    private Integer vncport;

    @ApiModelProperty(value = "vncipv6端口")
    private String vncportIpv6;

    @ApiModelProperty(value = "HA状态 0:正常 1:未加入HA 2:已加入HA，配置文件不同步")
    private Integer haStatus;

    @ApiModelProperty(value = "HA状态 0：未被HA管理，1：被HA管理")
    private Integer haManage;

    @ApiModelProperty(value = "vnc代理")
    private String vncProxy;

    private String pae;

    private String acpi;

    private String apic;

    @ApiModelProperty(value = "cpu调度优先级")
    private String cpuTune;

    @ApiModelProperty(value = " io调度优先级")
    private String ioTune;

    @ApiModelProperty(value = "memory调度优先级")
    private String memoryTune;

    @ApiModelProperty(value = "虚拟机是否允许自动迁移")
    private String autoMigrate;

    @ApiModelProperty(value = "是否启用保护模式")
    private String protectModel;

    @ApiModelProperty(value = "是否启用保护模式 1启用  0不启用")
    private Integer protect;

    @ApiModelProperty(value = "主机状态")
    private Integer hostStatus;

    @ApiModelProperty(value = " 前台界面主机维护模式 3 维护模式 1 正常模式")
    private Integer pageHostStatus;

    @ApiModelProperty(value = "虚拟机密级")
    private String secretLevel;

    @ApiModelProperty(value = "是否在保密策略安全级别中")
    private Boolean isSecret;

    @ApiModelProperty(value = "是否有加密磁盘")
    private Boolean diskSecret;

    private Boolean isClusterSafety;

    private Boolean existPrealloc;

    private Boolean existRaw;

    @ApiModelProperty(value = "是否容灾机")
    private Boolean isSrmVm;

    @ApiModelProperty(value = "虚拟机分类")
    private Integer vmType;

    @ApiModelProperty(value = "是否被纳管")
    private Integer manageExistVm;

    @ApiModelProperty(value = "虚拟机是否是K8S集群节点虚拟机，0-否，1是")
    private Integer kaasVm;

    @ApiModelProperty(value = "是否开启防病毒")
    private Boolean enableAntivirus;

    @ApiModelProperty(value = "防病毒状态")
    private String antivirusStatus;

    @ApiModelProperty(value = "主机所属的延展主机组")
    private String currentStretchedHostGroupName;

    @ApiModelProperty(value = "虚拟机所属的延展主机组")
    private String shouldInStretchedHostGroupName;

    @ApiModelProperty(value = "是否开启HA")
    private Integer haEnable;
}
