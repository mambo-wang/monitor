package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "实时告警信息")
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
    @Schema(description = "告警id")
    private Long id;
    @Schema(hidden = true)
    private Long warnId;
    @Schema(hidden = true)
    private Long resourceId;
    @Schema(description = "确认状态")
    private Integer state;
    @Schema(description = "告警级别")
    private Integer eventLevel;
    @Schema(description = "告警类型")
    private Integer eventType;
    @Schema(description = "告警名称")
    private String eventName;
    @Schema(description = "告警来源")
    private String eventSrc;
    @Schema(description = "最新告警时间")
    private Long eventTime;
    @Schema(description = "首次告警时间")
    private Long firstEventTime;
    @Schema(description = "告警信息")
    private String eventDesc;
    @Schema(hidden = true)
    private Long targetId;
    @Schema(hidden = true)
    private String uuid;
    @Schema(hidden = true)
    private String childTarget;
    @Schema(description = "告警次数")
    private Integer eventCount;
    @Schema(description = "告警确认时间")
    private Long confirmTime;
    @Schema(hidden = true)
    private Integer category;
    @Schema(hidden = true)
    private Integer catalogId;
}
