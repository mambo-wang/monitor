package com.virtual.cloud.om.agent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
//相当于数据库里的表名
public class AgentVersion {
    private String version;
    @ApiModelProperty("升级记录id")
    private Long recordId;
    @ApiModelProperty("升级结果 0-失败，1-成功")
    private Integer status;
    @ApiModelProperty("原因")
    private String desc;
    @ApiModelProperty(value ="租户id")
    private String watcherCode;
    @ApiModelProperty(value = "升级包id")
    private Long packageId;
    @ApiModelProperty(value = "uuid")
    private String uuid;
    @ApiModelProperty(value = "userId")
    private String userId;
}
