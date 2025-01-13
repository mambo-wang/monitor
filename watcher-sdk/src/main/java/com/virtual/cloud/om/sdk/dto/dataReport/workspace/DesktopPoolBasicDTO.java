package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DesktopPoolBasicDTO {
    @ApiModelProperty(value = "桌面池id", example = "8")
    private Long desktopPoolId;
    @ApiModelProperty(value = "桌面池名称", example = "桌面池1")
    private String name;
    @ApiModelProperty(value = "分配模式 1：静态，2：动态，3：手工 4:匿名登录", example = "1")
    private Integer assignMode;
    @ApiModelProperty(value = "计算机类型：0 虚拟机, 1 物理机 2胖终端 3 VOI", example = "0")
    private Integer computerType;
    @ApiModelProperty(value = "用户类型，0:本地用户，1:域用户，2：设备用户", example = "0")
    private Integer userType;
    @ApiModelProperty(value = "部署目标类型，0集群，1主机", example = "1")
    private Integer targetType;
    @ApiModelProperty(value = "集群名称", example = "测试集群")
    private String clusterName;
    @ApiModelProperty(value = "桌面名称前缀", example = "cas-")
    private String desktopNamePrefix;
    @ApiModelProperty(value = "最大虚拟机数量", example = "200")
    private Integer maxVmNum;
    @ApiModelProperty(value = "系统盘存储名称", example = "存储池1")
    private String storagePoolName;
    @ApiModelProperty(value = "数据盘存储名称", example = "第二存储池")
    private String secondStoragePoolName;
    @ApiModelProperty(value = "部署CVK名称")
    private String cvkName;
    @ApiModelProperty(value = "模板镜像名称")
    private String templateName;
    @ApiModelProperty(value="模板镜像cpu")
    private String templateCpu;
    @ApiModelProperty(value="模板镜像cpu")
    private Integer templateMemory;
    private Integer runningNum;
    private Integer pausedNum;
    private Integer abnormalNum;
    private Integer unknownNum;
    private Integer shutOffNum;
    private Integer vmNum;
    private Integer allocationNum;
    private Integer noAllocationNum;
    private Integer onlineNumber;
}
