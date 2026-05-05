package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
@ApiModel("网卡信息DTO")
public class NetworkInfoDTO implements Serializable {

    private static final long serialVersionUID = -3915019507409961214L;

    @ApiModelProperty("ip地址")
    private String ipAddr;

    @ApiModelProperty("网卡名称")
    private String name;

    public NetworkInfoDTO() {
    }

    public NetworkInfoDTO(String ipAddr, String name) {
        this.ipAddr = ipAddr;
        this.name = name;
    }

}
