package com.virtual.cloud.om.sdk.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/13 16:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebsocketAuthDTO {

    private String token;

    private String heartbeat;

    private String watcherCode;
}
