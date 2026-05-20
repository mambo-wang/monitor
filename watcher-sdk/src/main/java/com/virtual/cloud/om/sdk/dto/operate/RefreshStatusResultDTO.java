package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class RefreshStatusResultDTO {
    @Data
    public static class Domain {
        @Schema(description = ("虚拟机操作"))
        private String domainId;
        @Schema(description = ("服务器操作、存储池操作"))
        private Integer domainStatus;
        @Schema(description = ("资源id"))
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class Host {
        @Schema(description = ("服务器操作"))
        private String hostId;
        @Schema(description = ("服务器操作"))
        private Integer maintain;
        @Schema(description = ("服务器操作"))
        private Integer hostStatus;
        @Schema(description = ("服务器操作"))
        private Integer cvkMaintain;
        @Schema(description = ("资源id"))
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class DesktopPool {
        @Schema(description = ("桌面池操作"))
        private Long desktopPoolId;
        @Schema(description = ("桌面池操作"))
        private Integer type;
        @Schema(description = ("桌面池操作"))
        private List<OperateResultDTO.DataDTO.ResultObject.Target> targetList;
        @Schema(description = ("已分配虚拟机总数"))
        private Integer allocationNum;
        @Schema(description = ("未分配虚拟机总数"))
        private Integer noAllocationNum;
        @Schema(description = ("已接入个数"))
        private Integer onlineNumber;
        @Schema(description = ("资源id"))
        private String resourceId;
        private String uuid;
        private String failMsg;
    }

    @Data
    public static class Device {
        @Schema(description = ("终端操作"))
        private Long deviceId;
        @Schema(description = ("终端操作"))
        private Integer deviceStatus;
        @Schema(description = ("资源id"))
        private String resourceId;
        private String uuid;
        private String failMsg;
    }
}
