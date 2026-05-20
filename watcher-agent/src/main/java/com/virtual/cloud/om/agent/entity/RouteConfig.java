package com.virtual.cloud.om.agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;



import java.util.List;

@Data
@Schema
@Builder

public class RouteConfig {
    
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
    private Long createTime;
    private Long updateTime;
}
