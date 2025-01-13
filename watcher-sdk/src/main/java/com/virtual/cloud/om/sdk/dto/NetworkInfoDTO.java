package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("网卡信息DTO")
public class NetworkInfoDTO implements Serializable {

    private static final long serialVersionUID = -3915019507409961214L;

    @ApiModelProperty("ip地址")
    private String ipAddr;

    @ApiModelProperty("网卡名称")
    private String name;

}
