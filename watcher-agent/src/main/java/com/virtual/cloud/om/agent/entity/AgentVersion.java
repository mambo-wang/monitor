package com.virtual.cloud.om.agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = ("升级记录id"))
    private Long recordId;
    @Schema(description = ("升级结果 0-失败，1-成功"))
    private Integer status;
    @Schema(description = ("原因"))
    private String desc;
    @Schema(description ="租户id")
    private String watcherCode;
    @Schema(description = "升级包id")
    private Long packageId;
    @Schema(description = "uuid")
    private String uuid;
    @Schema(description = "userId")
    private String userId;
}
