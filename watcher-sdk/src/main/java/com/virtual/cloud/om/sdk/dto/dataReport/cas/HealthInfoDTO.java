package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@ToString
@XmlRootElement(name = "health")
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthInfoDTO {
    private static final long serialVersionUID = 7630696172783821368L;
    /** cpu健康度 */
    @ApiModelProperty(value="cpuHealth")
    private Long cpuHealth;

    /** 内存健康度*/
    @ApiModelProperty(value="memHealth")
    private Long memHealth;

    /** 存储健康度*/
    @ApiModelProperty(value="storageHealth")
    private Long storageHealth;

    /** 网络健康度*/
    @ApiModelProperty(value="netHealth")
    private Long netHealth;

    /** cvk健康度*/
    @ApiModelProperty(value="cvkHealth")
    private Long cvkHealth;

    /** 主机id*/
    @ApiModelProperty(value="hostId")
    private Long hostId;

    /** 主机名称*/
    @ApiModelProperty(value="hostName")
    private String hostName;

    /** 管理平台健康度*/
    @ApiModelProperty(value="cvmHealth")
    private Long cvmHealth;
}
