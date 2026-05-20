package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@Schema
public class VmTaskLogDto {
    @Schema(description = ("地址"))
    private String address;
    @Schema(description = ("类别"))
    private String category;
    @Schema(description = ("描述"))
    private String description;
    @Schema(description = ("事件"))
    private String event;
    @Schema(description = ("失败原因"))
    private String failureReason;
    @Schema(description = ("id"))
    private Integer id;
    @Schema(description = ("等级"))
    private Integer level;
    @Schema(description = ("登陆名"))
    private String loginName;
    @Schema(description = ("操作开始时间"))
    private Long operStartTime;
    @Schema(description = ("操作时间"))
    private Long operTime;
    @Schema(description = ("进展状态"))
    private Integer progress;
    @Schema(description = ("执行结果,0：成功  1：部分成功 2：失败"))
    private Integer result;
    @Schema(description = ("目标名字"))
    private String targetName;
    @Schema(description = ("用户名"))
    private String userName;



}
