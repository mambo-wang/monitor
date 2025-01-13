package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class HostBasicDTO {

    private static final long serialVersionUID = -5732555124697253492L;

    /** 物理机ID。 */
    @ApiModelProperty(value="id",example="1")
    private Long id = null;
    /** 集群ID。 */
    @ApiModelProperty(value="集群ID",example="1")
    private Long clusterId = null;
    /** 物理机名称。 * */
    @ApiModelProperty(value="物理机名称",example="1")
    private String name;
    /** IP地址。 * */
    @ApiModelProperty(value="IP地址",example="1")
    private String ipAddress;
    /** 主机提供商。 * */
    @ApiModelProperty(value="主机提供商",example="1")
    private String provider;
    /** 访问主机用户名。 */
    @ApiModelProperty(value="访问主机用户名",example="1")
    private String hostUser;
    /** 访问主机密码。 */
    @ApiModelProperty(value="主机",example="1")
    private String pw;
    /** CPU个数，32位整数类型（int） * */
    @ApiModelProperty(value="CPU个数",example="32")
    private Integer cpu;
    /** cpu 核数 */
    @ApiModelProperty(value="cpu核数",example="1")
    private Integer cpuCores;
    /** cpu 个数 */
    @ApiModelProperty(value="cpu个数",example="1")
    private Integer cpuSockets;
    /** CPU频率，单位为MHz。* */
    @ApiModelProperty(value="CPU频率，单位为MHz",example="1")
    private Integer cpuFrequency;
    /** CPU提供商。 */
    @ApiModelProperty(value="CPU提供商",example="1")
    private String cpuProvider;
    /** CPU信息。 */
    @ApiModelProperty(value="CPU信息",example="1")
    private String cpuDetail;
    /** 内存大小，单位为MB。 * */
    @ApiModelProperty(value="内存大小，单位为MB",example="1")
    private Long totalMemory;
    /** 剩余内存大小，单位为MB。 * */
    @ApiModelProperty(value="剩余内存大小，单位为MB",example="1")
    private Long freeMemory;
    /**
     * 存储总容量。 *
     */
    @ApiModelProperty(value="存储总容量",example="1")
    private Long storage;
    /**
     * 可用主机存储容量。 *
     */
    @ApiModelProperty(value="可用主机存储容量",example="1")
    private Long freeStorage;

    /**
     * 磁盘利用率
     */
    @ApiModelProperty(value="磁盘利用率",example="1.03")
    private double diskRate;

    /** 主机版本 */
    @ApiModelProperty(value="主机版本")
    private String version;
    /** 状态 */
    @ApiModelProperty(value="状态",example="1")
    private Integer status;
    /** 加入管理平台时间 */
    @ApiModelProperty(value="加入管理平台时间",example="1")
    private String addTime;
    /** 主机系统时间 */
    @ApiModelProperty(value="主机系统时间",example="1")
    private String hostSystemTime;
    /** 运行时间 */
    @ApiModelProperty(value="运行时间")
    private String runTime;
    /**
     * CPU分配比。
     * 虚拟机CPU总核数 / 物理机总核数
     */
    @ApiModelProperty(value="虚拟机CPU分配率 = 虚拟机CPU总核数 / 物理机总核数",example="1.11")
    private double cpuSuperRatio;
    /** 进入维护模式的方式 */
    @ApiModelProperty(value="进入维护模式的方式",example="1")
    private Integer maintain;

    /** cvk进入维护模式的方式 */
    @ApiModelProperty(value="cvk进入维护模式的方式",example="1")
    private Integer cvkMaintain  ;
    /**
     * 内存分配比。
     * 虚拟机总内存 / 物理机总内存
     */
    @ApiModelProperty(value="虚拟机内存分配率 = 虚拟机总内存 / 物理机总内存",example="1.32")
    private double memSuperRatio;
    /** 主机iLO地址 */
    @ApiModelProperty(value="主机iLO地址",example="1")
    private String[] iLOs;

    @ApiModelProperty(value="是否在HA中",example="1")
    private Integer haEnable;
}
