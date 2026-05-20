package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = ("用户"))
    private String user;
    @Schema(description = ("主机名称"))
    private String name;
    @Schema(description = ("主机ip"))
    private String ip;
    @Schema(description = ("服务器型号"))
    private String model;
    @Schema(description = ("服务器制造商"))
    private String vendor;
    @Schema(description = ("cpu型号"))
    private String cpuModel;
    @Schema(description = ("cpu主频"))
    private String cpuFrequence;
    @Schema(description = ("磁盘大小MB"))
    private String diskSize;
    @Schema(description = ("内存大小MB"))
    private String memorySize;
    @Schema(description = ("cpu总数"))
    private String cpuNum;
    @Schema(description = ("虚拟机数量"))
    private String vmNum;
    @Schema(description = ("运行数量"))
    private String vmRunCount;
    @Schema(description = ("关闭数量"))
    private String vmShutoff;
    @Schema(description = ("主机状态"))
    private String status;
    @Schema(description = ("运行时间"))
    private String runTime;
    @Schema(description = ("主机系统时间"))
    private String systemTime;
    @Schema(description = ("主机版本"))
    private String version;
    @Schema(description = ("主机安全区域"))
    private String isSafeArea;
    @Schema(description = ("CPU频率.单位为GHz.用于前台显示概要信息"))
    private String cpuFrequenceGhz;
    @Schema(description = ("内存大小带单位"))
    private String memory;
    @Schema(description = ("可用内存带单位"))
    private String freeMemory;
    @Schema(description = ("磁盘大小带单位"))
    private String storage;
    @Schema(description = ("可用主机存储容量带单位"))
    private String freeStorage;
    @Schema(description = ("cpu分配比"))
    private String cpuSuperRatio;
    @Schema(description = ("内存分配比"))
    private String memorySuperRatio;
    @Schema(description = ("是否有存储节点"))
    private String enableStorNode;
    @Schema(description = ("是否有备份网络"))
    private String enableBackupNetwork;
    @Schema(description = ("主机存储使用率统计/vms分区信息"))
    private String occRate;
    @Schema(description = ("主机iLO地址:多个用;分割"))
    private String iLOs;
}
