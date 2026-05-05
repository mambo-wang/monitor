package com.virtual.cloud.om.sdk.dto.operate;

import com.virtual.cloud.om.sdk.dto.dataReport.cas.ShareFileHostBasicDTOCopy;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OperateResultDTO {
    @ApiModelProperty("租户id")
    private String watcherCode;
    private String comCode;
    private String orgCode;
    @ApiModelProperty("上报内容")
    private List<DataDTO> data;

    @Data
    public static class DataDTO {
        @ApiModelProperty("资源id")
        private String resourceId;
        @ApiModelProperty("命令目标类型")
        private Integer objectType;
        @ApiModelProperty("目标")
        private ResultObject object;
        @ApiModelProperty("操作类型")
        private Integer operateType;
        @ApiModelProperty("远程命令唯一标识")
        private String uuid;
        @ApiModelProperty("执行结果,0：成功  1：部分成功 2：失败")
        private Integer result;
        @ApiModelProperty("失败原因")
        private String failureMessage;
        @ApiModelProperty("采集端操作")
        private String watcherIp;

        public enum ResultEnum {
            success(0, "成功"),
            part_success(1, "部分成功"),
            fail(2, "失败"),
            ;

            public final Integer val;
            public final String desc;

            ResultEnum(Integer val, String desc) {
                this.val = val;
                this.desc = desc;
            }
        }

        @Data
        public static class ResultObject {
            @ApiModelProperty("虚拟机操作")
            private String domainId;
            @ApiModelProperty("虚拟机操作")
            private Integer domainStatus;
            @ApiModelProperty("终端操作")
            private Long deviceId;
            @ApiModelProperty("终端操作")
            private Integer deviceStatus;
            @ApiModelProperty("服务器操作、存储池操作")
            private String hostId;
            @ApiModelProperty("存储池操作")
            private String poolName;
            @ApiModelProperty("存储池操作")
            private Integer poolStatus;
            @ApiModelProperty("存储池操作、共享存储操作")
            private Long totalSize;
            @ApiModelProperty("存储池操作、共享存储操作")
            private Double allocation;
            @ApiModelProperty("存储池操作、共享存储操作")
            private Long freeSize;
            @ApiModelProperty("集群操作、共享存储操作")
            private String clusterId;
            @ApiModelProperty("共享存储操作")
            private List<ShareFileHostBasicDTOCopy> shareFileHostList;
            @ApiModelProperty("共享存储操作")
            private String shareFileName;
            @ApiModelProperty("共享存储操作")
            private String shareFileType;
            @ApiModelProperty("服务器操作")
            private Integer maintain;
            @ApiModelProperty("服务器操作")
            private Integer hostStatus;
            @ApiModelProperty("服务器操作")
            private Integer cvkMaintain;
            @ApiModelProperty("桌面池操作")
            private Long desktopPoolId;
            @ApiModelProperty("桌面池操作")
            private Integer desktopPoolType;
            @ApiModelProperty("桌面池操作")
            private List<Target> targetList;
            @ApiModelProperty("采集端操作")
            private Integer componentType;
            @ApiModelProperty("采集端操作")
            private Integer componentStatus;

            @Data
            public static class Target{
                private String targetId;
                private Integer targetStatus;
            }
        }
    }
}
