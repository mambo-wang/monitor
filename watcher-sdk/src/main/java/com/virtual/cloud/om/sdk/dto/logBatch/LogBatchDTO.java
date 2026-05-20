package com.virtual.cloud.om.sdk.dto.logBatch;

import com.virtual.cloud.om.sdk.constant.LogBatchTypeEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class LogBatchDTO {
    @Schema(description = ("ticket"))
    private String ticket;
    @Schema(description = ("平台类型"))
    private ReportResourceEnum platform;
    @Schema(description = ("开始日期"))
    private Integer time;
    @Schema(description = ("资源范围"))
    private String range;
    @Schema(description = ("文件名"))
    private String fileName;
    private List<LogFileInfoDTO> data;

    @Data
    @Schema
    public static class LogFileInfoDTO{
        @Schema(description = ("日志类型"))
        private LogBatchTypeEnum logType;
        @Schema(description = ("平台资源id"))
        private String resourceId;
        @Schema(description = ("收集日志的目标"))
        private LogBatchTargetsQueryDTO[] targets;
        @Schema(description = ("能力中心Agent-IP"))
        private String watcherIp;
    }
}
