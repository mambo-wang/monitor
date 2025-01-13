package com.virtual.cloud.om.sdk.dto.token;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel("与平台之间进行token认证的http请求交互的token信息")
@Data
@Document(value = "ResourceToken")
@CompoundIndex(name = "resource_host_unq", def = "{'resource': 1, 'host': 1}", unique = true)
public class ResourceHttpClientToken {
    @Id
    private String mid;
    private String resource;
    private String host;
    private String token;
    private String createTime;
    private Long updateTimeMs;
    private String sessionId;
}
