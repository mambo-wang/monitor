package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@ApiModel("版本信息")
@XmlRootElement(name = "versionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlatformVersionDTO {
    @ApiModelProperty(value = "版本", example = "V7.0 (D0899)")
    private String casVersion;
}
