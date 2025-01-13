package com.virtual.cloud.om.sdk.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebsocketHostLimitDTO {
    private String ticket;
    private String message;
}
