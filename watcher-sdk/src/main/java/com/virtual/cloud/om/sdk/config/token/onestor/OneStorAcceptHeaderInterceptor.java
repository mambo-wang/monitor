package com.virtual.cloud.om.sdk.config.token.onestor;

import com.google.common.base.Charsets;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OneStorAcceptHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        request.getHeaders().setAccept(acceptHeaders());
        List<String> list = request.getHeaders().get("content-type");
        if (!CollectionUtils.isEmpty(list) && list.get(0).contains("multipart/form-data")) {
            return execute(execution, request, body);
        }
        request.getHeaders().remove("content-type");
        request.getHeaders().add("content-type", "application/json");
        return execute(execution, request, body);
    }

    private ClientHttpResponse execute(ClientHttpRequestExecution execution, HttpRequest request, byte[] body) {
        try {
            return execution.execute(request, body);
        } catch (ConnectTimeoutException e) {
            log.error("[http error] request execute uri={} ConnectTimeoutException error", request.getURI(), e);
            throw new AppException(ErrorCodes.HOST_CONNECT_TIMEOUT, request.getURI());
        } catch (Exception e) {
            log.error("[http error] request execute uri={} error", request.getURI(), e);
            throw new AppException(ErrorCodes.HOST_CONNECT_ERROR, request.getURI());
        }
    }

    private List<MediaType> acceptHeaders() {
        List<MediaType> types = new ArrayList<>();
        types.add(new MediaType("application", "xml", Charsets.UTF_8));
        types.add(new MediaType("application", "json", Charsets.UTF_8));
        return types;
    }
}
