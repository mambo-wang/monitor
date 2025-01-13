package com.virtual.cloud.om.sdk.dto.websocket;

import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class WebsocketPushDTO<T> {
    private final WebsocketPushTypeEnum type;
    private final T data;
}
