package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class HostBasicDTO {

    private static final long serialVersionUID = -5732555124697253492L;

    /** 物理机ID。 */
    @Schema(description="id",example="1")
    private Long id = null;
    /** 集群ID。 */
    @Schema(description="集群ID",example="1")
    private Long clusterId = null;
    /** 物理机名称。 * */
    @Schema(description="物理机名称",example="1")
    private String name;
    /** IP地址。 * */
    @Schema(description="IP地址",example="1")
    private String ipAddress;
    /** 主机提供商。 * */
    @Schema(description="主机提供商",example="1")
    private String provider;
    /** 访问主机用户名。 */
    @Schema(description="访问主机用户名",example="1")
    private String hostUser;
    /** 访问主机密码。 */
    @Schema(description="主机",example="1")
    private String pw;
    /** CPU个数，32位整数类型（int） * */
    @Schema(description="CPU个数",example="32")
    private Integer cpu;
    /** cpu 核数 */
    @Schema(description="cpu核数",example="1")
    private Integer cpuCores;
    /** cpu 个数 */
    @Schema(description="cpu个数",example="1")
    private Integer cpuSockets;
    /** CPU频率，单位为MHz。* */
    @Schema(description="CPU频率，单位为MHz",example="1")
    private Integer cpuFrequency;
    /** CPU提供商。 */
    @Schema(description="CPU提供商",example="1")
    private String cpuProvider;
    /** CPU信息。 */
    @Schema(description="CPU信息",example="1")
    private String cpuDetail;
    /** 内存大小，单位为MB。 * */
    @Schema(description="内存大小，单位为MB",example="1")
    private Long totalMemory;
    /** 剩余内存大小，单位为MB。 * */
    @Schema(description="剩余内存大小，单位为MB",example="1")
    private Long freeMemory;
    /**
     * 存储总容量。 *
     */
    @Schema(description="存储总容量",example="1")
    private Long storage;
    /**
     * 可用主机存储容量。 *
     */
    @Schema(description="可用主机存储容量",example="1")
    private Long freeStorage;

    /**
     * 磁盘利用率
     */
    @Schema(description="磁盘利用率",example="1.03")
    private double diskRate;

    /** 主机版本 */
    @Schema(description="主机版本")
    private String version;
    /** 状态 */
    @Schema(description="状态",example="1")
    private Integer status;
    /** 加入管理平台时间 */
    @Schema(description="加入管理平台时间",example="1")
    private String addTime;
    /** 主机系统时间 */
    @Schema(description="主机系统时间",example="1")
    private String hostSystemTime;
    /** 运行时间 */
    @Schema(description="运行时间")
    private String runTime;
    /**
     * CPU分配比。
     * 虚拟机CPU总核数 / 物理机总核数
     */
    @Schema(description="虚拟机CPU分配率 = 虚拟机CPU总核数 / 物理机总核数",example="1.11")
    private double cpuSuperRatio;
    /** 进入维护模式的方式 */
    @Schema(description="进入维护模式的方式",example="1")
    private Integer maintain;

    /** cvk进入维护模式的方式 */
    @Schema(description="cvk进入维护模式的方式",example="1")
    private Integer cvkMaintain  ;
    /**
     * 内存分配比。
     * 虚拟机总内存 / 物理机总内存
     */
    @Schema(description="虚拟机内存分配率 = 虚拟机总内存 / 物理机总内存",example="1.32")
    private double memSuperRatio;
    /** 主机iLO地址 */
    @Schema(description="主机iLO地址",example="1")
    private String[] iLOs;

    @Schema(description="是否在HA中",example="1")
    private Integer haEnable;
}
