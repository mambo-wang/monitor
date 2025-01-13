package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "终端告警")
public class DeviceAlarmDTO implements Serializable {

    private static final long serialVersionUID = -3419638091974415652L;

    private Long id;

    private String deviceName;

    private Integer eventType;

    private Integer status;

    private Integer emailEnable;

    private Long firstAlarmTime;

    private Long lastAlarmTime;

    private Long recoverTime;

    private String alarmSource;

    private String description;

    @JsonProperty("projectId")
    private String tenantId;

    private String projectName;
}
