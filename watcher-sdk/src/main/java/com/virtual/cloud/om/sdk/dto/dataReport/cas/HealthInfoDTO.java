package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description="cpuHealth")
    private Long cpuHealth;

    /** 内存健康度*/
    @Schema(description="memHealth")
    private Long memHealth;

    /** 存储健康度*/
    @Schema(description="storageHealth")
    private Long storageHealth;

    /** 网络健康度*/
    @Schema(description="netHealth")
    private Long netHealth;

    /** cvk健康度*/
    @Schema(description="cvkHealth")
    private Long cvkHealth;

    /** 主机id*/
    @Schema(description="hostId")
    private Long hostId;

    /** 主机名称*/
    @Schema(description="hostName")
    private String hostName;

    /** 管理平台健康度*/
    @Schema(description="cvmHealth")
    private Long cvmHealth;
}
