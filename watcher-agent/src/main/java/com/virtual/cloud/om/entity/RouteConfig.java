package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@ApiModel
@Builder
@Document(collection = "RouteConfig")
public class RouteConfig {
    @Id
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
    private Long createTime;
    private Long updateTime;
}
