package com.virtual.cloud.om.sdk.config.rest.common;

import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.SneakyThrows;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * 自定义的Spring Rest Template异常处理器
 */
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateErrorHandler.class);

    @SneakyThrows
    @Override
    public void handleError(ClientHttpResponse response) {
        try {
            super.handleError(response);
        } catch (ConnectTimeoutException | HttpHostConnectException e) {
            log.error("[http error] response error", e);
            throw new AppException(ErrorCodes.HOST_CONNECT_ERROR, response.getStatusCode().getReasonPhrase());
        } catch (HttpServerErrorException e) {
            log.error("[http error] response error", e);
            throw new AppException(ErrorCodes.BAD_GATEWAY, response.getStatusCode().getReasonPhrase());
        } catch (HttpClientErrorException e) {
            if(e.getClass() == HttpClientErrorException.Unauthorized.class){
                throw new AppException(ErrorCodes.UNAUTHORIZED);
            }
            log.error("[http error] response error", e);
            if(e.getClass() == HttpClientErrorException.NotFound.class){
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
            if(e.getClass() == HttpClientErrorException.Forbidden.class){
                throw new AppException(ErrorCodes.FORBIDDEN);
            }
            if(e.getClass() == HttpClientErrorException.UnsupportedMediaType.class){
                throw new AppException(ErrorCodes.HTTP_UNSUPPORT_MEDIA_TYPE);
            }
            if(e.getClass() == HttpClientErrorException.Conflict.class){
                throw e;
            }
            throw new AppException(ErrorCodes.HTTP_CLIENT_ERROR, response.getStatusCode().getReasonPhrase());
        } catch (Exception e) {
            log.warn("[http error] response error", e);
            throw new AppException(ErrorCodes.SERVER_INTERNAL_ERROR, response.getStatusCode().getReasonPhrase());
        }
    }
}
