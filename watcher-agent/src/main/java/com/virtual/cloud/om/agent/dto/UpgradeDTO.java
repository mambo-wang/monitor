package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/7/27 19:20
 */
@Data
@Schema
public class UpgradeDTO implements Serializable {
    private static final long serialVersionUID = -7890211834435775363L;
    @Schema(description = ("升级包id，数据中心的记录"))
    private Long packageId;
    @Schema(description = ("升级包记录id，运维中心的记录"))
    private Long recordId;

    private String md5;
    private String uuid;

    @Schema(description = ("租户id"))
    private String userId;

}
