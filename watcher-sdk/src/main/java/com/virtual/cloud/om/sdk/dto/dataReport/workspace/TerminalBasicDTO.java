package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TerminalBasicDTO {
    @ApiModelProperty("设备id")
    private String deviceId;
    @ApiModelProperty("设备唯一标识（目前为MAC地址）")
    private String deviceUuid;
    @ApiModelProperty("设备显示名称")
    private String displayName;
    @ApiModelProperty("IP地址")
    private String ipAddress;
    @ApiModelProperty("IP地址")
    private String macAddress;
    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("黑名单用户")
    private Boolean isDenyList;
    @ApiModelProperty("终端注册时间")
    private Long deviceRegisterTime;
    @ApiModelProperty("操作系统类型")
    private Integer osType;
    @ApiModelProperty("设备CPU架构")
    private String cpuArch;
    @ApiModelProperty("设备厂商")
    private String vendor;
    @ApiModelProperty("设备型号")
    private String model;
    @ApiModelProperty("客户端版本号")
    private String clientVersion;
    @ApiModelProperty("设备id")
    private String spaceAgentVersion;
    @ApiModelProperty("客户单所在操作系统版本")
    private String osVersion;
    @ApiModelProperty("客户端设备sn")
    private String sn;
    @ApiModelProperty("终端类型")
    private Integer deviceType;
    @ApiModelProperty("登录类型")
    private Integer authType;
    @ApiModelProperty("终端在线")
    private Integer status;
    @ApiModelProperty("设备分组名字")
    private String deviceGroupName;
    @ApiModelProperty("设备分组ID")
    private Long deviceGroupId;
}
