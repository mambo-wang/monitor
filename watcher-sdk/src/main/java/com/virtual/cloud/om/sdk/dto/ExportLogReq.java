package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created by x19765 on 2022/8/17.
 */
@Data
public class ExportLogReq {

    @Schema(description="平台类型（workspace/cas/uis/hccAgent）")
    private String platform;

    @Schema(description="资源ID")
    private Long resourceId;

    @Schema(description="资源类型:all/host/vm/terminal")
    private String type;

    @Schema(description="目标ID")
    private Long targetId;

    @Schema(description="日志路径")
    private String path;

    @Schema(description = "KQL查询语句")
    private String query;

    @Schema(description = "查询起始时间")
    private Long startTime;

    @Schema(description = "查询结束时间")
    private Long endTime;

    @Schema(description = "0:csv，1:json")
    private Integer dataFormat;

    @Schema(description = "0：全部日志  非0：日志数量（不能大于总日志数量）")
    private Integer logNum;

    @Schema(description = "根据时间排序 0:asc 1:desc")
    private Integer sortDir;

    private String level;
}

