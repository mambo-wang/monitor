package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "虚拟机ID")
    private Long id = null;

    @Schema(description = "虚拟机显示名称")
    private String title;

    @Schema(description = "虚拟机名称")
    private String domainName;

    private Boolean isEdit;

    @Schema(description = "主机名称")
    private String hostName;

    @Schema(description="主机ip",example="1")
    private String hostIp;

    @Schema(description = "虚拟机状态。取值： 0:模板 1:未知 2:运行 3:关闭 4 暂停。")
    private Integer status;

    @Schema(description="操作系统版本")
    private String osVersion;

    @Schema(description = "虚拟机安装的操作系统。取值：0:Windows;1:Linux,2:BSD")
    private Integer system;

    @Schema(description = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    @Schema(description="虚拟机cpu颗数",example="1")
    private Integer cpuSocket;

    @Schema(description="cpu核数",example="1")
    private Integer cpuCore;

    @Schema(description = "虚拟机内存")
    private String memory;

    @Schema(description = "虚拟机CPU利用率")
    private Double cpuRate;

    @Schema(description = "虚拟机内存利用率")
    private Double memoryRate;

    @Schema(description="虚拟机存储 带单位 用于前台显示概要信息",example="1")
    private String storage;

    @Schema(description = "castools状态,用于页面显示 0:未运行 1:运行")
    private String castools;

    @Schema(description = "castools是否运行 0:未运行 1:运行")
    private Boolean castoolRunning;

    @Schema(description = "castools状态 0:未运行 1:运行")
    private Integer castoolStatus;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "虚拟机运行时间 单位分钟，-1表示未知")
    private int uptime = 0;

    @Schema(description = "最后关机时间 单位分钟，-1表示未知")
    private int lastOffTime = 0;

    @Schema(description = "虚拟机UUID")
    private String uuid;

    @Schema(description = "虚拟机系统位数。取值： x86_64 x86")
    private String osBit;

    @Schema(description = "显示类型")
    private String displayType;

    @Schema(description = "自动配置")
    private String autoConfig;

    @Schema(description = " vnc端口")
    private Integer vncport;

    @Schema(description = "vncipv6端口")
    private String vncportIpv6;

    @Schema(description = "HA状态 0:正常 1:未加入HA 2:已加入HA，配置文件不同步")
    private Integer haStatus;

    @Schema(description = "HA状态 0：未被HA管理，1：被HA管理")
    private Integer haManage;

    @Schema(description = "vnc代理")
    private String vncProxy;

    private String pae;

    private String acpi;

    private String apic;

    @Schema(description = "cpu调度优先级")
    private String cpuTune;

    @Schema(description = " io调度优先级")
    private String ioTune;

    @Schema(description = "memory调度优先级")
    private String memoryTune;

    @Schema(description = "虚拟机是否允许自动迁移")
    private String autoMigrate;

    @Schema(description = "是否启用保护模式")
    private String protectModel;

    @Schema(description = "是否启用保护模式 1启用  0不启用")
    private Integer protect;

    @Schema(description = "主机状态")
    private Integer hostStatus;

    @Schema(description = " 前台界面主机维护模式 3 维护模式 1 正常模式")
    private Integer pageHostStatus;

    @Schema(description = "虚拟机密级")
    private String secretLevel;

    @Schema(description = "是否在保密策略安全级别中")
    private Boolean isSecret;

    @Schema(description = "是否有加密磁盘")
    private Boolean diskSecret;

    private Boolean isClusterSafety;

    private Boolean existPrealloc;

    private Boolean existRaw;

    @Schema(description = "是否容灾机")
    private Boolean isSrmVm;

    @Schema(description = "虚拟机分类")
    private Integer vmType;

    @Schema(description = "是否被纳管")
    private Integer manageExistVm;

    @Schema(description = "虚拟机是否是K8S集群节点虚拟机，0-否，1是")
    private Integer kaasVm;

    @Schema(description = "是否开启防病毒")
    private Boolean enableAntivirus;

    @Schema(description = "防病毒状态")
    private String antivirusStatus;

    @Schema(description = "主机所属的延展主机组")
    private String currentStretchedHostGroupName;

    @Schema(description = "虚拟机所属的延展主机组")
    private String shouldInStretchedHostGroupName;

    @Schema(description = "是否开启HA")
    private Integer haEnable;
}
