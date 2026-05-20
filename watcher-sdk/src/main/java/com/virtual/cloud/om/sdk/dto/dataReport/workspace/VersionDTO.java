package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "版本信息")
public class VersionDTO implements Serializable {

    private static final long serialVersionUID = -2153823115957117169L;

    @Schema(description = "外部版本号")
    private String outVersion;

    @Schema(description = "CAS外部版本号")
    private String casOutVersion;

    @Schema(description = "编译版本号")
    private String buildVersion;

    @Schema(description = "公司品牌")
    private String vendorName;
}
