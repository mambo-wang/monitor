package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("主机信息")
@XmlRootElement(name = "host")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 物理机ID。 */
    @ApiModelProperty(value="id",example="1")
    private Long id = null;
    /** 访问主机用户名。 */
    @ApiModelProperty(value="访问主机用户名",example="1")
    private String user;
    /** 访问主机密码。 */
    @ApiModelProperty(value="访问主机密码",example="1")
    private String pwd;
    /** 主机池ID。*/
    @ApiModelProperty(value = "主机池ID")
    private Long hostPoolId = 0L;
    /** 集群ID。 */
    @ApiModelProperty(value="集群ID",example="1")
    private Long clusterId = null;
    /** 物理机名称。 * */
    @ApiModelProperty(value="物理机名称",example="1")
    private String name;
    /** IP地址。 * */
    @ApiModelProperty(value="IP地址",example="1")
    private String ip;
    /** CPU个数，32位整数类型（int） * */
    @ApiModelProperty(value="CPU个数",example="32")
    private Integer cpuCount;
    /*** 主机类型。**/
    @ApiModelProperty(value="主机类型",example="0")
    private Integer hostType;
    /** 内存大小，单位为MB。 * */
    @ApiModelProperty(value="内存大小，单位为MB",example="1")
    private Long memorySize;
    /** 主机版本 */
    @ApiModelProperty(value="主机版本")
    private String version;
    /** 状态 */
    @ApiModelProperty(value="状态",example="1")
    private Integer status;
    /**
     * 存储总容量。 *
     */
    @ApiModelProperty(value="存储总容量",example="1")
    private Long storageCapacity;
    /** HA标识 */
    @ApiModelProperty(value="HA标识",example="1")
    private Integer enableHA;
    /** mac地址 */
    @ApiModelProperty(value="mac地址",example="1")
    private String mcNetAddr;
    /** cpu 个数 */
    @ApiModelProperty(value="cpu个数",example="1")
    private Integer cpuSockets;
    /** cpu 核数 */
    @ApiModelProperty(value="cpu核数",example="1")
    private Integer cpuCores;
    /** 进入维护模式的方式 */
    @ApiModelProperty(value="进入维护模式的方式",example="1")
    private Integer maintainMode;
    @ApiModelProperty(value="是否在HA中",example="1")
    private Integer haEnable;
    /** 主机提供商。 * */
    @ApiModelProperty(value="主机提供商",example="1")
    private String provider;
    /** CPU频率，单位为MHz。* */
    @ApiModelProperty(value="CPU频率，单位为MHz",example="1")
    private Integer frequence;
    /** CPU提供商。 * */
    @ApiModelProperty(value="CPU提供商",example="1")
    private String cpuProvider;
    /** 主机已用存储容量，单位为MB。 * */
    @ApiModelProperty(value="主机已用存储容量，单位为MB",example="1")
    private Long allocation;
    /** 主机可用存储，单位为MB。 * */
    @ApiModelProperty(value="主机可用存储，单位为MB",example="1")
    private Long avilable;
    /** cvk进入维护模式的方式 */
    @ApiModelProperty(value="cvk进入维护模式的方式",example="1")
    private Integer cvkMaintain;
    /** iscsi节点名称。 * */
    @ApiModelProperty(value="iscsi节点名称",example="1")
    private String iscsiNodeName;
    /** CVK版本 */
    @ApiModelProperty(value="CVK版本")
    private String cvkVersion;
    /** 加入管理平台时间 */
    @ApiModelProperty(value="加入管理平台时间",example="1")
    private Date addTime;
    /** 主机是否为HA故障切换主机：0，不是；1：是 */
    @ApiModelProperty(value="主机是否为HA故障切换主机：0，不是；1：是",example="1")
    private Boolean haResource;
    /** 主机iLO地址 */
    @ApiModelProperty(value="主机iLO地址",example="1")
    private String iLOs;
    /** 存储ip。 * */
    @ApiModelProperty(value="存储ip",example="1")
    private String storageIp;
}
