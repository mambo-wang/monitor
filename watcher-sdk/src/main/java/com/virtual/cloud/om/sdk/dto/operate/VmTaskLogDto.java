package com.virtual.cloud.om.sdk.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@ApiModel
public class VmTaskLogDto {
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("类别")
    private String category;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("事件")
    private String event;
    @ApiModelProperty("失败原因")
    private String failureReason;
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("等级")
    private Integer level;
    @ApiModelProperty("登陆名")
    private String loginName;
    @ApiModelProperty("操作开始时间")
    private Long operStartTime;
    @ApiModelProperty("操作时间")
    private Long operTime;
    @ApiModelProperty("进展状态")
    private Integer progress;
    @ApiModelProperty("执行结果,0：成功  1：部分成功 2：失败")
    private Integer result;
    @ApiModelProperty("目标名字")
    private String targetName;
    @ApiModelProperty("用户名")
    private String userName;



}
