package com.virtual.cloud.om.uis.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.PlatformTestConnectionApi;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import com.virtual.cloud.om.sdk.utils.uis.WebDecryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UisTestConnectionApi implements PlatformTestConnectionApi {
    private final UisRestConnection uisRestConnection;

    @Override
    public String connection(String platform, String ipAddress, Integer port, String username, String pwd, String protocol, String authTyp) {
        String uri = String.format(UisUriConstants.LOGIN, WebDecryptUtils.encryptData(username), WebDecryptUtils.encryptData(pwd));
        String rpcResult = this.uisRestConnection.post(ipAddress, protocol, username, pwd, port, uri,"", new ParameterizedTypeReference<String>() {
        }).getBody();
        JSONObject result = JSONUtil.parseObj(rpcResult);
        // 返回字段不够通用，暂不新建实体类
        return result.getInt("loginFailErrorCode").equals(0) ? null : result.getStr("loginFailMessage");
    }

    @Override
    public ReportResourceEnum platform() {
        return ReportResourceEnum.uis;
    }
}
