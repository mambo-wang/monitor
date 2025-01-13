package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/21 15:14
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class HostOverviewDTO implements Serializable {
    private static final long serialVersionUID = -4087764724633023054L;
    @ApiModelProperty("用户")
    private String user;
    @ApiModelProperty("主机名称")
    private String name;
    @ApiModelProperty("主机ip")
    private String ip;
    @ApiModelProperty("服务器型号")
    private String model;
    @ApiModelProperty("服务器制造商")
    private String vendor;
    @ApiModelProperty("cpu型号")
    private String cpuModel;
    @ApiModelProperty("cpu主频")
    private String cpuFrequence;
    @ApiModelProperty("磁盘大小MB")
    private String diskSize;
    @ApiModelProperty("内存大小MB")
    private String memorySize;
    @ApiModelProperty("cpu总数")
    private String cpuNum;
    @ApiModelProperty("虚拟机数量")
    private String vmNum;
    @ApiModelProperty("运行数量")
    private String vmRunCount;
    @ApiModelProperty("关闭数量")
    private String vmShutoff;
    @ApiModelProperty("主机状态")
    private String status;
    @ApiModelProperty("运行时间")
    private String runTime;
    @ApiModelProperty("主机系统时间")
    private String systemTime;
    @ApiModelProperty("主机版本")
    private String version;
    @ApiModelProperty("主机安全区域")
    private String isSafeArea;
    @ApiModelProperty("CPU频率.单位为GHz.用于前台显示概要信息")
    private String cpuFrequenceGhz;
    @ApiModelProperty("内存大小带单位")
    private String memory;
    @ApiModelProperty("可用内存带单位")
    private String freeMemory;
    @ApiModelProperty("磁盘大小带单位")
    private String storage;
    @ApiModelProperty("可用主机存储容量带单位")
    private String freeStorage;
    @ApiModelProperty("cpu分配比")
    private String cpuSuperRatio;
    @ApiModelProperty("内存分配比")
    private String memorySuperRatio;
    @ApiModelProperty("是否有存储节点")
    private String enableStorNode;
    @ApiModelProperty("是否有备份网络")
    private String enableBackupNetwork;
    @ApiModelProperty("主机存储使用率统计/vms分区信息")
    private String occRate;
    @ApiModelProperty("主机iLO地址:多个用;分割")
    private String iLOs;
}
