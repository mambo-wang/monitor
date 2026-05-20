package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class TerminalBasicDTO {
    @Schema(description = ("设备id"))
    private String deviceId;
    @Schema(description = ("设备唯一标识（目前为MAC地址）"))
    private String deviceUuid;
    @Schema(description = ("设备显示名称"))
    private String displayName;
    @Schema(description = ("IP地址"))
    private String ipAddress;
    @Schema(description = ("IP地址"))
    private String macAddress;
    @Schema(description = ("设备名称"))
    private String deviceName;
    @Schema(description = ("黑名单用户"))
    private Boolean isDenyList;
    @Schema(description = ("终端注册时间"))
    private Long deviceRegisterTime;
    @Schema(description = ("操作系统类型"))
    private Integer osType;
    @Schema(description = ("设备CPU架构"))
    private String cpuArch;
    @Schema(description = ("设备厂商"))
    private String vendor;
    @Schema(description = ("设备型号"))
    private String model;
    @Schema(description = ("客户端版本号"))
    private String clientVersion;
    @Schema(description = ("设备id"))
    private String spaceAgentVersion;
    @Schema(description = ("客户单所在操作系统版本"))
    private String osVersion;
    @Schema(description = ("客户端设备sn"))
    private String sn;
    @Schema(description = ("终端类型"))
    private Integer deviceType;
    @Schema(description = ("登录类型"))
    private Integer authType;
    @Schema(description = ("终端在线"))
    private Integer status;
    @Schema(description = ("设备分组名字"))
    private String deviceGroupName;
    @Schema(description = ("设备分组ID"))
    private Long deviceGroupId;
}
