package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by x19765 on 2022/8/17.
 */
@Data
public class ExportLogReq {

    @ApiModelProperty(value="平台类型（workspace/cas/uis/hccAgent）")
    private String platform;

    @ApiModelProperty(value="资源ID")
    private Long resourceId;

    @ApiModelProperty(value="资源类型:all/host/vm/terminal")
    private String type;

    @ApiModelProperty(value="目标ID")
    private Long targetId;

    @ApiModelProperty(value="日志路径")
    private String path;

    @ApiModelProperty(value = "KQL查询语句")
    private String query;

    @ApiModelProperty(value = "查询起始时间")
    private Long startTime;

    @ApiModelProperty(value = "查询结束时间")
    private Long endTime;

    @ApiModelProperty(value = "0:csv，1:json")
    private Integer dataFormat;

    @ApiModelProperty(value = "0：全部日志  非0：日志数量（不能大于总日志数量）")
    private Integer logNum;

    @ApiModelProperty(value = "根据时间排序 0:asc 1:desc")
    private Integer sortDir;

    private String level;
}

