package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @author kf9535
 * @version 1.0
 * @date 2022/6/17 14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WebsocketSate {

    
    private String id;

    private Integer state;

    private String message;
}
