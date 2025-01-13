package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class OperateQueryDTO {
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("命令目标类型")
    private Integer objectType;
    @ApiModelProperty("目标")
    private TargetObject object;
    @ApiModelProperty("操作类型")
    private Integer operateType;
    @ApiModelProperty("远程命令唯一标识")
    private String uuid;

    @Data
    public static class TargetObject{
        @ApiModelProperty("虚拟机操作")
        private String domainId;
        @ApiModelProperty("服务器操作、存储池操作")
        private String hostId;
        @ApiModelProperty("存储池操作")
        private String poolName;
        @ApiModelProperty("集群操作、服务器操作、共享存储操作")
        private String clusterId;
        @ApiModelProperty("共享存储操作")
        private String shareFileName;
        @ApiModelProperty("共享存储操作")
        private String shareFileType;
        @ApiModelProperty("主机进入维护模式的方式" +
                "0:不迁移主机上虚拟机" +
                "1：自动迁移主机上运行或暂停的虚拟机到其它主机" +
                "2：自动迁移主机上关闭的虚拟机到其它主机" +
                "3：自动迁移主机上运行或暂停、关闭的虚拟机到其它主机")
        private Integer maintainMode;
        @ApiModelProperty("终端操作")
        private Long deviceId;
        @ApiModelProperty("桌面池操作")
        private Long desktopPoolId;
        @ApiModelProperty("采集端操作")
        private String watcherIp;
        @ApiModelProperty("采集端操作")
        private Integer componentType;
    }
}
