package com.virtual.cloud.om.sdk.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/6/17 14:17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebsocketStateDTO {

    /**state  : 0 成功， 2 失败 */
    private Integer state;

    private String successMessage;

    private String failureMessage;
}
