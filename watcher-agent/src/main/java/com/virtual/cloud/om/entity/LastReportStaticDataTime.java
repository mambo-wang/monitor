package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "LastReportStaticDataTime")//相当于数据库里的表名
@ApiModel
public class LastReportStaticDataTime {
    @Id
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("最后上报时间")
    private Long lastTimeMs;
}
