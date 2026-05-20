package com.virtual.cloud.om.sdk.dto.operate;

import com.virtual.cloud.om.sdk.dto.dataReport.cas.ShareFileHostBasicDTOCopy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class OperateResultDTO {
    @Schema(description = ("租户id"))
    private String watcherCode;
    private String comCode;
    private String orgCode;
    @Schema(description = ("上报内容"))
    private List<DataDTO> data;

    @Data
    public static class DataDTO {
        @Schema(description = ("资源id"))
        private String resourceId;
        @Schema(description = ("命令目标类型"))
        private Integer objectType;
        @Schema(description = ("目标"))
        private ResultObject object;
        @Schema(description = ("操作类型"))
        private Integer operateType;
        @Schema(description = ("远程命令唯一标识"))
        private String uuid;
        @Schema(description = ("执行结果,0：成功  1：部分成功 2：失败"))
        private Integer result;
        @Schema(description = ("失败原因"))
        private String failureMessage;
        @Schema(description = ("采集端操作"))
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
            @Schema(description = ("虚拟机操作"))
            private String domainId;
            @Schema(description = ("虚拟机操作"))
            private Integer domainStatus;
            @Schema(description = ("终端操作"))
            private Long deviceId;
            @Schema(description = ("终端操作"))
            private Integer deviceStatus;
            @Schema(description = ("服务器操作、存储池操作"))
            private String hostId;
            @Schema(description = ("存储池操作"))
            private String poolName;
            @Schema(description = ("存储池操作"))
            private Integer poolStatus;
            @Schema(description = ("存储池操作、共享存储操作"))
            private Long totalSize;
            @Schema(description = ("存储池操作、共享存储操作"))
            private Double allocation;
            @Schema(description = ("存储池操作、共享存储操作"))
            private Long freeSize;
            @Schema(description = ("集群操作、共享存储操作"))
            private String clusterId;
            @Schema(description = ("共享存储操作"))
            private List<ShareFileHostBasicDTOCopy> shareFileHostList;
            @Schema(description = ("共享存储操作"))
            private String shareFileName;
            @Schema(description = ("共享存储操作"))
            private String shareFileType;
            @Schema(description = ("服务器操作"))
            private Integer maintain;
            @Schema(description = ("服务器操作"))
            private Integer hostStatus;
            @Schema(description = ("服务器操作"))
            private Integer cvkMaintain;
            @Schema(description = ("桌面池操作"))
            private Long desktopPoolId;
            @Schema(description = ("桌面池操作"))
            private Integer desktopPoolType;
            @Schema(description = ("桌面池操作"))
            private List<Target> targetList;
            @Schema(description = ("采集端操作"))
            private Integer componentType;
            @Schema(description = ("采集端操作"))
            private Integer componentStatus;

            @Data
            public static class Target{
                private String targetId;
                private Integer targetStatus;
            }
        }
    }
}
