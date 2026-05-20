package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/7/28 14:10
 */
@Data
@Schema
public class UpgradeResultDTO implements Serializable {
    private static final long serialVersionUID = 4758835525370683437L;
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
