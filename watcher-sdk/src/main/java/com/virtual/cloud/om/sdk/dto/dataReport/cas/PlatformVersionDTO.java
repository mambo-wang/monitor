package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Schema(description = "版本信息")
@XmlRootElement(name = "versionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlatformVersionDTO {
    @Schema(description = "版本", example = "V7.0 (D0899)")
    private String casVersion;
}
