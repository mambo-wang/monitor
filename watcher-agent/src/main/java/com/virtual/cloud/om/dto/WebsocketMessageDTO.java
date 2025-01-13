package com.virtual.cloud.om.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/14 9:46
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebsocketMessageDTO {

    /**消息类型*/
    private String type;

    /**消息内容*/
    private String data;
}
