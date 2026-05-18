package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "版本信息")
public class VersionDTO implements Serializable {

    private static final long serialVersionUID = -2153823115957117169L;

    @ApiModelProperty(value = "外部版本号")
    private String outVersion;

    @ApiModelProperty(value = "CAS外部版本号")
    private String casOutVersion;

    @ApiModelProperty(value = "编译版本号")
    private String buildVersion;

    @ApiModelProperty(value = "公司品牌")
    private String vendorName;
}
