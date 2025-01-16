package com.virtual.cloud.om.agent.dto;

import lombok.Data;

@Data
public class WebsocketWatcherRouteOperateResult {
    public static final Integer FAILED = 0;
    public static final Integer SUCCESS = 1;

    private Integer state;
    private String uuid;

}
