package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.RestHost;

import java.util.List;

public interface ResourceApi {
    RestHost findRestHostByResourceId(String resourceId);

    List<RestHost> findAll();
}
