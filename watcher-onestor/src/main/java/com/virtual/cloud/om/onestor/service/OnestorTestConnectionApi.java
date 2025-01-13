package com.virtual.cloud.om.onestor.service;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpStatus;
import com.virtual.cloud.om.sdk.api.PlatformTestConnectionApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorTokenResult;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OnestorLoginEntityDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnestorTestConnectionApi implements PlatformTestConnectionApi {
    private final OnestorRestConnection onestorRestConnection;
    private final RestTemplate restTemplate;

    @Override
    public String connection(String platform, String ipAddress, Integer port, String username, String pwd, String protocol, String authTyp) {
        //OneStor没有专用的测试，故直接调用登录接口看返回是否登录成功
        String tokenUrl = protocol + "://" + ipAddress + ":" + port + OnestoreUriConstants.auth.AUTH;
        ResponseEntity response = restTemplate.exchange(tokenUrl, HttpMethod.GET, null, String.class);
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        String token = cookies.stream().filter(v -> v.startsWith("XSRF-TOKEN")).findAny().orElse("");
        token = StringUtils.substringBefore(token, ";");
        String sessionId = cookies.stream().filter(v -> v.startsWith("calamari_sessionid")).findAny().orElse("");
        sessionId = StringUtils.substringBefore(sessionId, ";");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        headers.add(HttpHeaders.COOKIE, sessionId+";"+token);
        headers.add("X-XSRF-TOKEN", token.replace("XSRF-TOKEN=",""));
        OnestorLoginEntityDTO onestorLoginEntityDTO = new OnestorLoginEntityDTO();
        onestorLoginEntityDTO.setUsername(username);
        onestorLoginEntityDTO.setPassword(Base64Encoder.encode(pwd));
        HttpEntity<OnestorLoginEntityDTO> requestEntity = new HttpEntity<>(onestorLoginEntityDTO, headers);
        ResponseEntity<OnestorTokenResult> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, OnestorTokenResult.class);
        if(!(responseEntity.getStatusCodeValue() == HttpStatus.HTTP_OK)){
            throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR, tokenUrl);
        }
        return null;
    }

    @Override
    public ReportResourceEnum platform() {
        return ReportResourceEnum.onestor;
    }
}
