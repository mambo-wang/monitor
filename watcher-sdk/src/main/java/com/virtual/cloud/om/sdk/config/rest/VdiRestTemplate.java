package com.virtual.cloud.om.sdk.config.rest;

import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;

/**
 * 继承Spring原来的RestTemplate，把泛型参数名字减短一点
 */
@Slf4j
public class VdiRestTemplate extends RestTemplate {

    public VdiRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
                                          RestType<T> responseType, Object... uriVariables) throws RestClientException {
        try {
            Type type = responseType.getType();
            RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
            ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
            return execute(url, method, requestCallback, responseExtractor, uriVariables);
        } catch (AppException e) {
            if (e.getErrorCode().equals(ErrorCodes.NOT_FOUND)) {
                throw new AppException(e.getErrorCode(), url);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            log.error("request error ", e);
            throw new AppException(ErrorCodes.REST_CLIENT_ERROR);
        }
    }

}
