package com.virtual.cloud.om.agent.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//相当于数据库里的表名
@ApiModel
public class LastReportStaticDataTime {
    
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("最后上报时间")
    private Long lastTimeMs;
}
