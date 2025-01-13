package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class NetworkVO {
    private String name;
    private String ip;
    private String mask;
    private String gateway;
    private Integer wifi;
}
