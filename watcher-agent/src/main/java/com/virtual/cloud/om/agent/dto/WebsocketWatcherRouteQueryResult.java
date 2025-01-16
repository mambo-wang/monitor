package com.virtual.cloud.om.agent.dto;

import com.virtual.cloud.om.sdk.dto.deploy.RouteVo;
import lombok.Data;

import java.util.List;

@Data
public class WebsocketWatcherRouteQueryResult {
    public static final Integer FAILED = 0;
    public static final Integer SUCCESS = 1;

    private Integer state;
    private String uuid;
    private List<RouteVo> watcherRoutes;
}
