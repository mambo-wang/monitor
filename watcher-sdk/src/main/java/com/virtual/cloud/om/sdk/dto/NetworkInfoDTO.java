package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "网卡信息DTO")
public class NetworkInfoDTO implements Serializable {

    private static final long serialVersionUID = -3915019507409961214L;

    @Schema(description = ("ip地址"))
    private String ipAddr;

    @Schema(description = ("网卡名称"))
    private String name;

    public NetworkInfoDTO() {
    }

    public NetworkInfoDTO(String ipAddr, String name) {
        this.ipAddr = ipAddr;
        this.name = name;
    }

}
