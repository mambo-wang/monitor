package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel(value = "vip桌面告警")
public class VipDeskAlarmDTO implements Serializable {

    private Long id;

    @ApiModelProperty(name = "桌面名称")
    private String deskName;

    @ApiModelProperty(name = "授权用户")
    private String loginName;

    @ApiModelProperty(name = "事件类型")
    private Integer alarmType;

    @ApiModelProperty(name = "创建时间")
    private Long createTime;

    @ApiModelProperty(name = "是否发送邮件")
    private Integer emailEnable = 0;

    @ApiModelProperty(name = "恢复时间")
    private Long recoveryTime;

    @ApiModelProperty(name = "告警状态")
    private Integer status;

    @ApiModelProperty(name = "重复次数")
    private Integer count;

    @ApiModelProperty(name = "最新告警时间")
    private Long lastTime;

}
