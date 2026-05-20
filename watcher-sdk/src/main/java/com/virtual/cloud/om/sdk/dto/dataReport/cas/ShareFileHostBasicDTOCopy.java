package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class ShareFileHostBasicDTOCopy {

    @Schema(description = "主机ID")
    private long id;

    @Schema(description = "共享文件系统名称")
    private String fsName;

    @Schema(description = "标识符称")
    private String initiatorName;

    @Schema(description = "主机IP")
    private String hostIp;

    @Schema(description = "主机名称")
    private String hostName;

    @Schema(description = "主机状态")
    private Integer hostStatus;

    @Schema(description = "存储池状态")
    private Integer poolStatus;
}
