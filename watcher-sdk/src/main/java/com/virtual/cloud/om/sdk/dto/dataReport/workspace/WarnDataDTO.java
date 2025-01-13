package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class WarnDataDTO {
    @ApiModelProperty("告警类型")
    private Integer type;
    @ApiModelProperty("告警来源")
    private String src;
    @ApiModelProperty("告警名称")
    private String name;
    @ApiModelProperty("告警对象类别")
    private Integer objectType;
    @ApiModelProperty("告警信息内容")
    private String message;
    @ApiModelProperty("最新告警时间")
    private Long endsAt;
    @ApiModelProperty("首次告警时间")
    private Long startsAt;
    @ApiModelProperty(value = "告警级别")
    private Integer level;
    @ApiModelProperty(value = "告警重复次数")
    private Integer count;
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("能力中心Agent-ip")
    private String watcherIp;
}
