package com.virtual.cloud.om.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/6/17 14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "WebsocketSate")
public class WebsocketSate {

    @Id
    private String id;

    private Integer state;

    private String message;
}
