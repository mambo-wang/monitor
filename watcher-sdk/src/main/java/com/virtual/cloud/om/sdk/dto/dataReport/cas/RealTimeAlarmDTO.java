package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("实时告警")
@XmlRootElement(name = "alarm")
@XmlAccessorType(XmlAccessType.FIELD)
public class RealTimeAlarmDTO implements Serializable {

    private static final long serialVersionUID = -3419638091974415652L;

    @ApiModelProperty(value = "告警id")
    private Long id;
    @ApiModelProperty(hidden = true)
    private Integer catalogId;
    @ApiModelProperty(hidden = true)
    private Integer category;
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
    private Date eventTime;
    @ApiModelProperty(value = "告警信息")
    private String eventDesc;
    @ApiModelProperty(hidden = true)
    private Long targetId;
    @ApiModelProperty(hidden = true)
    private String uuid;
    @ApiModelProperty(value = "首次告警时间")
    private Date firstEventTime;
    @ApiModelProperty(value = "告警次数")
    private Integer eventCount;
}
