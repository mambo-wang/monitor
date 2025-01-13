package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@ApiModel
public class RouteVo {
    private String id;
    @ApiModelProperty("目的地址")
    private String targetIp;
    @ApiModelProperty("目的地址掩码")
    private String targetMask;
    @ApiModelProperty("下一跳地址")
    private String via;
    @ApiModelProperty("出接口")
    private String dev;
    @ApiModelProperty("适用采集端ip")
    private List<String> watchers;
    @ApiModelProperty("描述")
    private String desc;
    private String watcherCode;
    private String uuid;
}
