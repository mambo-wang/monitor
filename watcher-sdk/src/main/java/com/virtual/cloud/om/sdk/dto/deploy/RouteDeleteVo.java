package com.virtual.cloud.om.sdk.dto.deploy;

import lombok.Data;

import java.util.List;

@Data
public class RouteDeleteVo {
    private List<String> ids;
    private String watcherCode;
    private String uuid;
}
