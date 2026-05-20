package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class OperateQueryDTO {
    @Schema(description = ("资源id"))
    private String resourceId;
    @Schema(description = ("命令目标类型"))
    private Integer objectType;
    @Schema(description = ("目标"))
    private TargetObject object;
    @Schema(description = ("操作类型"))
    private Integer operateType;
    @Schema(description = ("远程命令唯一标识"))
    private String uuid;

    @Data
    public static class TargetObject{
        @Schema(description = ("虚拟机操作"))
        private String domainId;
        @Schema(description = ("服务器操作、存储池操作"))
        private String hostId;
        @Schema(description = ("存储池操作"))
        private String poolName;
        @Schema(description = ("集群操作、服务器操作、共享存储操作"))
        private String clusterId;
        @Schema(description = ("共享存储操作"))
        private String shareFileName;
        @Schema(description = ("共享存储操作"))
        private String shareFileType;
        @Schema(description = ("主机进入维护模式的方式" +
                "0:不迁移主机上虚拟机" +
                "1：自动迁移主机上运行或暂停的虚拟机到其它主机" +
                "2：自动迁移主机上关闭的虚拟机到其它主机" +
                "3：自动迁移主机上运行或暂停、关闭的虚拟机到其它主机"))
        private Integer maintainMode;
        @Schema(description = ("终端操作"))
        private Long deviceId;
        @Schema(description = ("桌面池操作"))
        private Long desktopPoolId;
        @Schema(description = ("采集端操作"))
        private String watcherIp;
        @Schema(description = ("采集端操作"))
        private Integer componentType;
    }
}
