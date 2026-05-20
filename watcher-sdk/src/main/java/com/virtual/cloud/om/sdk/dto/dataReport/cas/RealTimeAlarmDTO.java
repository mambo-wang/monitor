package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "实时告警")
@XmlRootElement(name = "alarm")
@XmlAccessorType(XmlAccessType.FIELD)
public class RealTimeAlarmDTO implements Serializable {

    private static final long serialVersionUID = -3419638091974415652L;

    @Schema(description = "告警id")
    private Long id;
    @Schema(hidden = true)
    private Integer catalogId;
    @Schema(hidden = true)
    private Integer category;
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
    private Date eventTime;
    @Schema(description = "告警信息")
    private String eventDesc;
    @Schema(hidden = true)
    private Long targetId;
    @Schema(hidden = true)
    private String uuid;
    @Schema(description = "首次告警时间")
    private Date firstEventTime;
    @Schema(description = "告警次数")
    private Integer eventCount;
}
