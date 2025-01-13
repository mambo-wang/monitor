package com.virtual.cloud.om.cas.service;

import com.virtual.cloud.om.sdk.api.PlatformTestConnectionApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CasTestConnectionApi implements PlatformTestConnectionApi {
    private final CasRestConnection casRestConnection;

    @Override
    public String connection(String platform, String ipAddress, Integer port, String username, String pwd, String protocol, String authTyp) {
        this.casRestConnection.get(platform, ipAddress, protocol, port, username, pwd, CasUriConstants.TEST_CONNECTION, new ParameterizedTypeReference<String>() {
        });
        return null;
    }

    @Override
    public ReportResourceEnum platform() {
        return ReportResourceEnum.cas;
    }
}
