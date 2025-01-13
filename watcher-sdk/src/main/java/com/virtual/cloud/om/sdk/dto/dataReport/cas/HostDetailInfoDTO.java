package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@ApiModel("指定主机信息")
@XmlRootElement(name = "host")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostDetailInfoDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;
    /** 访问主机用户名。 */
    @ApiModelProperty(value="访问主机用户名",example="1")
    private String user;
    /** 物理机名称。 * */
    @ApiModelProperty(value="物理机名称",example="1")
    private String name;
    /** IP地址。 * */
    @ApiModelProperty(value="IP地址",example="1")
    private String ip;
    /** 服务器型号 */
    @ApiModelProperty(value="服务器型号",example="1")
    private String model;
    /** 服务器制造商。 * */
    @ApiModelProperty(value="服务器制造商",example="1")
    private String vendor;
    /** CPU型号。 * */
    @ApiModelProperty(value="CPU型号",example="1")
    private String cpuModel;
    /** CPU频率，单位为MHz。* */
    @ApiModelProperty(value="CPU频率，单位为MHz",example="1")
    private Integer cpuFrequence;
    /** 磁盘大小，单位为MB。 * */
    @ApiModelProperty(value="磁盘大小，单位为MB",example="1")
    private Long diskSize;
    /** 内存大小，单位为MB。 * */
    @ApiModelProperty(value="内存大小，单位为MB",example="1")
    private Long memorySize;
    /** CPU数量 个数*核数 */
    @ApiModelProperty(value="CPU数量，个数*核数",example="1")
    private String cpuNum;
    /** 主机下虚拟机 启动关闭信息 */
    @ApiModelProperty(value="虚拟机个数",example="1")
    private Integer vmNum;
    @ApiModelProperty(value="运行虚拟机个数",example="1")
    private Integer vmRunCount;
    @ApiModelProperty(value="关闭虚拟机个数",example="1")
    private Integer vmShutoff;
    /** 状态 */
    @ApiModelProperty(value="状态",example="1")
    private Integer status;
    /** 运行时间 */
    @ApiModelProperty(value="运行时间")
    private String runTime;
    /** 主机系统时间 */
    @ApiModelProperty(value="主机系统时间",example="1")
    private String systemTime;
    /** 主机版本 */
    @ApiModelProperty(value="主机版本")
    private String version;
    /** 主机安全区域 */
    @ApiModelProperty(value="主机安全区域",example="false")
    private Boolean isSafeArea;
    /** CPU频率，单位为GHz。用于前台显示概要信息* */
    @ApiModelProperty(value="CPU频率，单位为GHz。用于前台显示概要信息",example="1")
    private String cpuFrequenceGhz;
    /** 内存大小 带单位 用于前台显示概要信息 */
    @ApiModelProperty(value="内存大小，带单位，用于前台显示概要信息",example="1")
    private String memory;
    /** 可用内存 带单位 用于前台显示概要信息 */
    @ApiModelProperty(value="可用内存 带单位，用于前台显示概要信息",example="1")
    private String freeMemory;
    /** 磁盘大小 带单位 用于前台显示概要信息 */
    @ApiModelProperty(value="磁盘大小，带单位，用于前台显示概要信息",example="1")
    private String storage;
    /** 可用主机存储容量  带单位 用于前台显示概要信息 */
    @ApiModelProperty(value="可用主机存储容量  带单位，用于前台显示概要信息",example="1")
    private String freeStorage;
    /**
     * CPU分配比。
     * 虚拟机CPU总核数 / 物理机总核数
     */
    @ApiModelProperty(value = "虚拟机CPU分配率 = 虚拟机CPU总核数 / 物理机总核数", example = "1.11")
    private double cpuSuperRatio;
    /**
     * 内存分配比。
     * 虚拟机总内存 / 物理机总内存
     */
    @ApiModelProperty(value = "虚拟机内存分配率 = 虚拟机总内存 / 物理机总内存", example = "1.32")
    private double memorySuperRatio;
    /** 是否有存储节点 */
    @ApiModelProperty(value="是否有存储节点",example="false")
    private Boolean enableStorNode;
    /** 是否有备份网络 */
    @ApiModelProperty(value="是否有备份网络",example="false")
    private Boolean enableBackupNetwork;
    /**主机存储使用率统计/vms分区信息*/
    @ApiModelProperty(value = "主机存储使用率统计/vms分区信息", example = "1.32")
    private double occRate;
    /** 主机iLO地址 */
    @ApiModelProperty(value="主机iLO地址",example="1")
    private String iLOs;
}
