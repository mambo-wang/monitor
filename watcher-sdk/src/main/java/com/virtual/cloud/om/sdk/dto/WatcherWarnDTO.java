package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WatcherWarnDTO implements Serializable {

    @Schema(description="节点id")
    private String deployId;

    @Schema(description="告警级别")
    private Integer level;

    @Schema(description="告警信息")
    private String message;

    /** 最新告警时间 */
    @Schema(description="最新告警时间")
    private Long lastTime;

    /** 告警类型 */
    @Schema(description="告警类型")
    private Integer type;

    /** 首次告警时间 */
    @Schema(description="首次告警时间")
    private Long firstTime;

    /** 告警重复次数 */
    @Schema(description="告警重复次数")
    private Long count;

}
