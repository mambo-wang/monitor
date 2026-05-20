package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class NetworkVO {
    private String name;
    private String ip;
    private String mask;
    private String gateway;
    private Integer wifi;
}
