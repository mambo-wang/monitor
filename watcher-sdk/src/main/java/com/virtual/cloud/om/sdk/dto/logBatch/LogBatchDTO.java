package com.virtual.cloud.om.sdk.dto.logBatch;

import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class LogBatchDTO {
    @ApiModelProperty("ticket")
    private String ticket;
    @ApiModelProperty("平台类型")
    private ReportResourceEnum platform;
    @ApiModelProperty("开始日期")
    private Integer time;
    @ApiModelProperty("资源范围")
    private String range;
    @ApiModelProperty("文件名")
    private String fileName;
    private List<LogFileInfoDTO> data;

    @Data
    @ApiModel
    public static class LogFileInfoDTO{
        @ApiModelProperty("日志类型")
        private LogBatchTypeEnum logType;
        @ApiModelProperty("平台资源id")
        private String resourceId;
        @ApiModelProperty("收集日志的目标")
        private LogBatchTargetsQueryDTO[] targets;
        @ApiModelProperty("能力中心Agent-IP")
        private String watcherIp;
    }
}
