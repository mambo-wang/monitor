package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("日志实时上报数据实体类")
public class FileBeatLogDTO {

    @ApiModelProperty(value = "日志文件路径")
    private String path;

    @ApiModelProperty(value = "日志内容")
    private String message;

    @ApiModelProperty(value = "日志产生时间")
    private String time;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "资源类型")
    private String platform;
}
