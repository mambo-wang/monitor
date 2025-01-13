package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.dto.RestHost;

/**
 * @Author: w22798
 * @Date: 2022/5/10 15:11
 */
public class MockDataUtil {

    public static RestHost queryResourceInfo(String resourceId) {
        RestHost restHost = RestHost.builder()
                .host("10.99.224.136")
                .password("Cloud@1234")
                .username("admin")
                .port(8083)
                .protocol("http")
                .platform("workspace")
                .resourceId("1")
                .build();
        return restHost;
    }
}
