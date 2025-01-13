package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ShareFileHostBasicDTOCopy {

    @ApiModelProperty(value = "主机ID")
    private long id;

    @ApiModelProperty(value = "共享文件系统名称")
    private String fsName;

    @ApiModelProperty(value = "标识符称")
    private String initiatorName;

    @ApiModelProperty(value = "主机IP")
    private String hostIp;

    @ApiModelProperty(value = "主机名称")
    private String hostName;

    @ApiModelProperty(value = "主机状态")
    private Integer hostStatus;

    @ApiModelProperty(value = "存储池状态")
    private Integer poolStatus;
}
