package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@Schema
public class RouteVo {
    private String id;
    @Schema(description = ("目的地址"))
    private String targetIp;
    @Schema(description = ("目的地址掩码"))
    private String targetMask;
    @Schema(description = ("下一跳地址"))
    private String via;
    @Schema(description = ("出接口"))
    private String dev;
    @Schema(description = ("适用采集端ip"))
    private List<String> watchers;
    @Schema(description = ("描述"))
    private String desc;
    private String watcherCode;
    private String uuid;
}
