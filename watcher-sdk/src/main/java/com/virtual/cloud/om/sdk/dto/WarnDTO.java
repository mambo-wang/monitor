package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WarnDTO implements Serializable {

    private static final long serialVersionUID = -7941942283954469788L;
    @ApiModelProperty(value="资源id")
    private String id;

    /** 最新告警时间 */
    @ApiModelProperty(value="最新告警时间")
    private Long eventTime;

    /** 告警类型 */
    @ApiModelProperty(value="告警类型")
    private String type;

    /** 最新上报时间 */
    @ApiModelProperty(value="最新上报时间")
    private Long reportTime;
}
