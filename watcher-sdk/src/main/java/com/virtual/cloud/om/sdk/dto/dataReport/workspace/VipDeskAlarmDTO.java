package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "vip桌面告警")
public class VipDeskAlarmDTO implements Serializable {

    private Long id;

    @Schema(description = "桌面名称")
    private String deskName;

    @Schema(description = "授权用户")
    private String loginName;

    @Schema(description = "事件类型")
    private Integer alarmType;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "是否发送邮件")
    private Integer emailEnable = 0;

    @Schema(description = "恢复时间")
    private Long recoveryTime;

    @Schema(description = "告警状态")
    private Integer status;

    @Schema(description = "重复次数")
    private Integer count;

    @Schema(description = "最新告警时间")
    private Long lastTime;

}
