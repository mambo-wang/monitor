package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("实时告警信息")
public class WarnInfoDTO {
    /**
     * id : 8
     * state : 2
     * eventLevel : 2
     * eventType : 30
     * eventName : 虚拟机内存异常告警
     * eventSrc : 虚拟机“win7”(null)
     * eventTime : 1574758989000
     * firstEventTime : 1574663978000
     * eventDesc : 虚拟机内存利用率超过20%，当前内存利用率37%。
     * targetId : 0
     * uuid : cc125b3c-cddd-4b17-aa53-ac1edd7bbe4c
     * childTarget :
     * eventCount : 1546
     * confirmTime : 0
     * category : 2
     * catalogId : 9
     */

    //workspace warn id
    @ApiModelProperty(value = "告警id")
    private Long id;
    @ApiModelProperty(hidden = true)
    private Long warnId;
    @ApiModelProperty(hidden = true)
    private Long resourceId;
    @ApiModelProperty(value = "确认状态")
    private Integer state;
    @ApiModelProperty(value = "告警级别")
    private Integer eventLevel;
    @ApiModelProperty(value = "告警类型")
    private Integer eventType;
    @ApiModelProperty(value = "告警名称")
    private String eventName;
    @ApiModelProperty(value = "告警来源")
    private String eventSrc;
    @ApiModelProperty(value = "最新告警时间")
    private Long eventTime;
    @ApiModelProperty(value = "首次告警时间")
    private Long firstEventTime;
    @ApiModelProperty(value = "告警信息")
    private String eventDesc;
    @ApiModelProperty(hidden = true)
    private Long targetId;
    @ApiModelProperty(hidden = true)
    private String uuid;
    @ApiModelProperty(hidden = true)
    private String childTarget;
    @ApiModelProperty(value = "告警次数")
    private Integer eventCount;
    @ApiModelProperty(value = "告警确认时间")
    private Long confirmTime;
    @ApiModelProperty(hidden = true)
    private Integer category;
    @ApiModelProperty(hidden = true)
    private Integer catalogId;
}
