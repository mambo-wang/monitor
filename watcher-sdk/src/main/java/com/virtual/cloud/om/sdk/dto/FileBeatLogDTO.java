package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: w22798
 * @Date: 2022/5/04 12:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "日志实时上报数据实体类")
public class FileBeatLogDTO {

    @Schema(description = "日志文件路径")
    private String path;

    @Schema(description = "日志内容")
    private String message;

    @Schema(description = "日志产生时间")
    private String time;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "资源类型")
    private String platform;
}
