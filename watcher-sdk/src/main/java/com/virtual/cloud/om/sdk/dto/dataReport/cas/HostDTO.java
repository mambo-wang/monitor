package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "主机信息")
@XmlRootElement(name = "host")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 物理机ID。 */
    @Schema(description="id",example="1")
    private Long id = null;
    /** 访问主机用户名。 */
    @Schema(description="访问主机用户名",example="1")
    private String user;
    /** 访问主机密码。 */
    @Schema(description="访问主机密码",example="1")
    private String pwd;
    /** 主机池ID。*/
    @Schema(description = "主机池ID")
    private Long hostPoolId = 0L;
    /** 集群ID。 */
    @Schema(description="集群ID",example="1")
    private Long clusterId = null;
    /** 物理机名称。 * */
    @Schema(description="物理机名称",example="1")
    private String name;
    /** IP地址。 * */
    @Schema(description="IP地址",example="1")
    private String ip;
    /** CPU个数，32位整数类型（int） * */
    @Schema(description="CPU个数",example="32")
    private Integer cpuCount;
    /*** 主机类型。**/
    @Schema(description="主机类型",example="0")
    private Integer hostType;
    /** 内存大小，单位为MB。 * */
    @Schema(description="内存大小，单位为MB",example="1")
    private Long memorySize;
    /** 主机版本 */
    @Schema(description="主机版本")
    private String version;
    /** 状态 */
    @Schema(description="状态",example="1")
    private Integer status;
    /**
     * 存储总容量。 *
     */
    @Schema(description="存储总容量",example="1")
    private Long storageCapacity;
    /** HA标识 */
    @Schema(description="HA标识",example="1")
    private Integer enableHA;
    /** mac地址 */
    @Schema(description="mac地址",example="1")
    private String mcNetAddr;
    /** cpu 个数 */
    @Schema(description="cpu个数",example="1")
    private Integer cpuSockets;
    /** cpu 核数 */
    @Schema(description="cpu核数",example="1")
    private Integer cpuCores;
    /** 进入维护模式的方式 */
    @Schema(description="进入维护模式的方式",example="1")
    private Integer maintainMode;
    @Schema(description="是否在HA中",example="1")
    private Integer haEnable;
    /** 主机提供商。 * */
    @Schema(description="主机提供商",example="1")
    private String provider;
    /** CPU频率，单位为MHz。* */
    @Schema(description="CPU频率，单位为MHz",example="1")
    private Integer frequence;
    /** CPU提供商。 * */
    @Schema(description="CPU提供商",example="1")
    private String cpuProvider;
    /** 主机已用存储容量，单位为MB。 * */
    @Schema(description="主机已用存储容量，单位为MB",example="1")
    private Long allocation;
    /** 主机可用存储，单位为MB。 * */
    @Schema(description="主机可用存储，单位为MB",example="1")
    private Long avilable;
    /** cvk进入维护模式的方式 */
    @Schema(description="cvk进入维护模式的方式",example="1")
    private Integer cvkMaintain;
    /** iscsi节点名称。 * */
    @Schema(description="iscsi节点名称",example="1")
    private String iscsiNodeName;
    /** CVK版本 */
    @Schema(description="CVK版本")
    private String cvkVersion;
    /** 加入管理平台时间 */
    @Schema(description="加入管理平台时间",example="1")
    private Date addTime;
    /** 主机是否为HA故障切换主机：0，不是；1：是 */
    @Schema(description="主机是否为HA故障切换主机：0，不是；1：是",example="1")
    private Boolean haResource;
    /** 主机iLO地址 */
    @Schema(description="主机iLO地址",example="1")
    private String iLOs;
    /** 存储ip。 * */
    @Schema(description="存储ip",example="1")
    private String storageIp;
}
