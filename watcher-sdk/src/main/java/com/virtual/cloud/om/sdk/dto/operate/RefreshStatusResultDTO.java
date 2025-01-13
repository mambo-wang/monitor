package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class RefreshStatusResultDTO {
    @Data
    public static class Domain {
        @ApiModelProperty("虚拟机操作")
        private String domainId;
        @ApiModelProperty("服务器操作、存储池操作")
        private Integer domainStatus;
        @ApiModelProperty("资源id")
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class Host {
        @ApiModelProperty("服务器操作")
        private String hostId;
        @ApiModelProperty("服务器操作")
        private Integer maintain;
        @ApiModelProperty("服务器操作")
        private Integer hostStatus;
        @ApiModelProperty("服务器操作")
        private Integer cvkMaintain;
        @ApiModelProperty("资源id")
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class DesktopPool {
        @ApiModelProperty("桌面池操作")
        private Long desktopPoolId;
        @ApiModelProperty("桌面池操作")
        private Integer type;
        @ApiModelProperty("桌面池操作")
        private List<OperateResultDTO.DataDTO.ResultObject.Target> targetList;
        @ApiModelProperty("已分配虚拟机总数")
        private Integer allocationNum;
        @ApiModelProperty("未分配虚拟机总数")
        private Integer noAllocationNum;
        @ApiModelProperty("已接入个数")
        private Integer onlineNumber;
        @ApiModelProperty("资源id")
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class Device {
        @ApiModelProperty("终端操作")
        private Long deviceId;
        @ApiModelProperty("终端操作")
        private Integer deviceStatus;
        @ApiModelProperty("资源id")
        private String resourceId;
        private String uuid;
        private String failMsg;
    }
}
