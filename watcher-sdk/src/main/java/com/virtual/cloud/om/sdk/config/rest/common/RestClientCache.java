package com.virtual.cloud.om.sdk.config.rest.common;

import com.virtual.cloud.om.sdk.dto.token.ResourceHttpClientToken;

import java.util.HashMap;
import java.util.Map;

public enum RestClientCache {
    INSTANCE;

    public Map<String, ResourceHttpClientToken> map = new HashMap<>();

    public void put(String key, ResourceHttpClientToken resourceHttpClientToken){

        map.put(key, resourceHttpClientToken);

    }

    public ResourceHttpClientToken get(String key){
        return map.get(key);
    }
}
