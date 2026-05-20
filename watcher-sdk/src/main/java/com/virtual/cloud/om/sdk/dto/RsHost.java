package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "host")
@XmlAccessorType(XmlAccessType.FIELD)
@Schema(description = "主机")
@Data
public class RsHost implements Serializable {

    private static final long serialVersionUID = -5732555124697253492L;

    /** 网络唤醒 。 */
    public static final int WEAK_UP_ON_LAN = 0;
    /** IPMI唤醒 。 */
    public static final int WEAK_UP_ON_IPMI = 1;

    /** 主机维护状态 。 */
    public static final int MAINTAIN_STATUS = 1;
    /** 主机维护状态（本地存储故障）。 */
    public static final int MAINTAIN_STATUS_STORAGE_FAILURE = 2;
    /** 主机维护状态 前台页面的值。 */
    public static final int PAGE_MAINTAIN_STATUS = 3;
    /** 主机维护状态（本地存储故障） 前台页面的值。 */
    public static final int PAGE_MAINTAIN_STATUS_STORAGE_FAILURE = 4;
    /** 主机非维护状态 。 */
    public static final int UNMAINTAIN_STATUS = 0;

    /** 主机是否在集群HA内。 */
    public static final int HOST_CLUSTER_STATUS = 2;
    /** 主机正常状态 。 */
    public static final int NORMAL_STATUS = 1;
    /** 主机不正常状态 。 */
    public static final int ABNORMITY_STATUS = 0;
    /** 主机不在HA集群中*/
    public static final int HA_DISENABLE = 2;

    /** 主机不在HA集群中*/
    public static final int HOST_HA_DISENABLED =0;
    /** 主机在HA集群中*/
    public static final int HOST_HA_ENABLED = 1;

    /** 物理机ID。 */
    @Schema(description="id",example="1")
    private Long id = null;
    /** 访问主机用户名。 */
    @Schema(description="访问主机用户名",example="1")
    private String user;
    /** 访问主机密码。 */
    @Schema(description="主机",example="1")
    private String pwd;

    /** 主机池机ID。 */
    @Schema(description="主机池机ID",example="1")
    private Long hostPoolId = null;

    /** 集群ID。 */
    @Schema(description="集群ID",example="1")
    private Long clusterId = null;

    /** 集群名称 */
    @Schema(description="集群名称",example="1")
    private String cluster;

    /** 物理机名称。 * */
    @Schema(description="物理机名称",example="1")
    private String name;

    /** 服务器支持的Hypervisor类型，如“kvm”、“xen”、“esx”。 * */
    @XmlAttribute
    @Schema(description="服务器支持的Hypervisor类型",example="kvm,xen,esx")
    private String type;

    /** 服务器IP地址。 * */
    @Schema(description="服务器IP地址",example="1")
    private String ip;

    /** 服务器型号。 * */
    @Schema(description="服务器型号",example="1")
    private String model;

    /** 服务器制造商。 * */
    @Schema(description="服务器制造商",example="1")
    private String vendor;

    /** CPU个数，32位整数类型（int） * */
    @Schema(description="CPU个数",example="32")
    private Integer cpuCount;

    /** CPU型号。 * */
    @Schema(description="CPU型号",example="1")
    private String cpuModel;

    /** CPU频率，单位为MHz。* */
    @Schema(description="CPU频率",example="1")
    private Integer cpuFrequence;

    /** 磁盘大小，单位为MB。 * */
    @Schema(description="磁盘大小",example="1")
    private Long diskSize;

    /** 内存大小，单位为MB。 * */
    @Schema(description="内存大小",example="1")
    private Long memorySize;

    /** CPU数量 个数*核数 */
    @Schema(description="CPU数量",example="1")
    private String cpuNum;

    /** cpu 个数 */
    @Schema(description="cpu 个数",example="1")
    private Integer cpuSockets;

    /** cpu 核数 */
    @Schema(description="cpu 核数",example="1")
    private Integer cpuCores;

    /** 主机下虚拟机 启动关闭信息 */
    @Schema(description="虚拟机个数",example="1")
    private Integer vmNum;
    @Schema(description="运行虚拟机个数",example="1")
    private Integer vmRunCount;
    @Schema(description="关闭虚拟机个数",example="1")
    private Integer vmShutoff;

    /** CPU利用率 */
    @Schema(description="CPU利用率",example="1")
    private Double cpuRate;
    /** 内存利用率 */
    @Schema(description="内存利用率",example="1")
    private Double memRate;
    /**网络IO吞吐量*/
    @Schema(description="网络IO吞吐量",example="1")
    private Double netIORate;
    /**磁盘IO吞吐量*/
    @Schema(description="磁盘IO吞吐量",example="1")
    private Double diskIORate;

    /** 状态 */
    @Schema(description="状态",example="1")
    private Integer status;
    /** 运行时间 */
    @Schema(description="运行时间")
    private String runTime;
    /** 主机版本 */
    @Schema(description="主机版本")
    private String version;

    /** CPU频率，单位为GHz。用于前台显示概要信息* */
    @Schema(description="主机",example="1")
    private String cpuFrequenceGhz;

    /** 内存大小 带单位 用于前台显示概要信息 */
    @Schema(description="主机",example="1")
    private String memory;
    /** 磁盘大小 带单位 用于前台显示概要信息 */
    @Schema(description="主机",example="1")
    private String storage;

    /** HA标识 */
    @Schema(description="主机",example="1")
    private Integer enableHA;
    /** 是否忽略重复管理 */
    @Schema(description="主机",example="1")
    private Boolean ignore;
    /** 主机的网段地址 */
    @Schema(description="主机",example="1")
    private String netAddr;
    /** 组播地址 */
    @Schema(description="主机",example="1")
    private String mcNetAddr;
    /** 端口 */
    @Schema(description="主机",example="1")
    private String mcPort;


    /** CPU周期最小限制 */
    @Schema(description="主机",example="1")
    private Integer cpuMinRate;
    /** CPU周期最大限制 */
    @Schema(description="主机",example="1")
    private Integer cpuMaxRate;
    /** 主机所在主机池名称 */
    @Schema(description="主机",example="1")
    private String hostPool;
    @Schema(description="主机",example="1")
    private String platForm;
    @Schema(description="主机",example="1")
    private Integer wakeCategory = null;
    @Schema(description="主机",example="1")
    private String ipmiIpaddr = null;
    @Schema(description="主机",example="1")
    private String ipmiUser = null;
    @Schema(description="主机",example="1")
    private String ipmiPw = null;

    /** 进入维护模式的方式 */
    @Schema(description="主机",example="1")
    private Integer maintainMode;

    @Schema(description="主机",example="1")
    private Integer haEnable;


    @Schema(description = ("所属资源"))
    private String resourceName;

    @Schema(description = ("所属资源ip"))
    private String resourceIp;
}

