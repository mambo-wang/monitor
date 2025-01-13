package com.virtual.cloud.om.sdk.dto.deploy;

import lombok.Data;

import java.util.List;

@Data
public class RouteCheckPingVo {
    private List<String> watchers;
    private String target;
    private String uuid;
}
