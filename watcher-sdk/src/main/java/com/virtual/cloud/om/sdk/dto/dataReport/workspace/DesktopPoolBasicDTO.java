package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class DesktopPoolBasicDTO {
    @Schema(description = "桌面池id", example = "8")
    private Long desktopPoolId;
    @Schema(description = "桌面池名称", example = "桌面池1")
    private String name;
    @Schema(description = "分配模式 1：静态，2：动态，3：手工 4:匿名登录", example = "1")
    private Integer assignMode;
    @Schema(description = "计算机类型：0 虚拟机, 1 物理机 2胖终端 3 VOI", example = "0")
    private Integer computerType;
    @Schema(description = "用户类型，0:本地用户，1:域用户，2：设备用户", example = "0")
    private Integer userType;
    @Schema(description = "部署目标类型，0集群，1主机", example = "1")
    private Integer targetType;
    @Schema(description = "集群名称", example = "测试集群")
    private String clusterName;
    @Schema(description = "桌面名称前缀", example = "cas-")
    private String desktopNamePrefix;
    @Schema(description = "最大虚拟机数量", example = "200")
    private Integer maxVmNum;
    @Schema(description = "系统盘存储名称", example = "存储池1")
    private String storagePoolName;
    @Schema(description = "数据盘存储名称", example = "第二存储池")
    private String secondStoragePoolName;
    @Schema(description = "部署CVK名称")
    private String cvkName;
    @Schema(description = "模板镜像名称")
    private String templateName;
    @Schema(description="模板镜像cpu")
    private String templateCpu;
    @Schema(description="模板镜像cpu")
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
