package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WatcherWarnDTO implements Serializable {

    @ApiModelProperty(value="节点id")
    private String deployId;

    @ApiModelProperty(value="告警级别")
    private Integer level;

    @ApiModelProperty(value="告警信息")
    private String message;

    /** 最新告警时间 */
    @ApiModelProperty(value="最新告警时间")
    private Long lastTime;

    /** 告警类型 */
    @ApiModelProperty(value="告警类型")
    private Integer type;

    /** 首次告警时间 */
    @ApiModelProperty(value="首次告警时间")
    private Long firstTime;

    /** 告警重复次数 */
    @ApiModelProperty(value="告警重复次数")
    private Long count;

}
