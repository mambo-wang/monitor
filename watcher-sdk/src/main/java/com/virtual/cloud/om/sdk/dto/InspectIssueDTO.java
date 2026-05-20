package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/11 14:44
 */

@Data
@Schema(description = "巡检指令下发")
public class InspectIssueDTO implements Serializable {
    private static final long serialVersionUID = 3745214104971329972L;

    @Schema(description = ("资源Id"))
    private String resourceId;
    @Schema(description = ("巡检类型"))
    private String inspectType;
    @Schema(description = ("确定唯一一次巡检"))
    private Long toolKitId;
    @Schema(description = ("巡检记录id"))
    private Long inspectRecordId;
    @Schema(description = ("巡检工具MD5"))
    private String identifier;
}
