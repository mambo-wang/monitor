package com.virtual.cloud.om.agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//相当于数据库里的表名
@Schema
public class LastReportStaticDataTime {
    
    @Schema(description = ("资源id"))
    private String resourceId;
    @Schema(description = ("最后上报时间"))
    private Long lastTimeMs;
}
