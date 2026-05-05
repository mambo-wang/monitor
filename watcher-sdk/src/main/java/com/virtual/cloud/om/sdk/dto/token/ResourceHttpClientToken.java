package com.virtual.cloud.om.sdk.dto.token;

import io.swagger.annotations.ApiModel;

@ApiModel("与平台之间进行token认证的http请求交互的token信息")
public class ResourceHttpClientToken {
    private String mid;
    private String resource;
    private String host;
    private String token;
    private String createTime;
    private Long updateTimeMs;
    private String sessionId;

    public String getMid() { return mid; }
    public void setMid(String mid) { this.mid = mid; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public Long getUpdateTimeMs() { return updateTimeMs; }
    public void setUpdateTimeMs(Long updateTimeMs) { this.updateTimeMs = updateTimeMs; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
