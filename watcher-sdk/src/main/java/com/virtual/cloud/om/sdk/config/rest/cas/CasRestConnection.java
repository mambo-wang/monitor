package com.virtual.cloud.om.sdk.config.rest.cas;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import com.virtual.cloud.om.sdk.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * REST连接获取MGR
 */
@Service("casRestConnection")
@ConditionalOnProperty(name = "rest-client.cas.enable", havingValue = "true", matchIfMissing = true)
public class CasRestConnection {
    /**
     * 日志
     */
    private static Logger log = LoggerFactory.getLogger(CasRestConnection.class);

    /**
     * 关闭时调用。
     */
    @PreDestroy
    public void destroy() {
        try {
            CasRestClientCache.get().shutdown();
        } catch (Exception e) {
            log.warn(null, e);
        }
    }

    public RestTemplate find(String ip, String protocol, Integer port, String username, String password) {
        Optional<CasRestClient> restClient = CasRestClientCache.get().get(ip, protocol, port, username, password);
        return restClient.<RestTemplate>map(CasRestClient::getRestTemplate).orElse(null);
    }


    public <T> T exchange(String host, String protocol, Integer port, String username, String password, String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchangeResp(host, protocol, port, username, password, url, method, requestEntity, responseType, uriVariables).getBody();
    }

    public <T> ResponseEntity<T> exchangeResp(String host, String protocol, Integer port, String username, String password, String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        RestTemplate restTemplate = find(host, protocol, port, username, password);
        if (restTemplate == null) {
            return null;
        }
        requestEntity = addRealIp(requestEntity);
        recordRequest(url, method, requestEntity);

        long startTime = System.currentTimeMillis();
        ResponseEntity<T> result = null;
        try {
            result = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
        } catch (AppException e) {
            if (e.getErrorCode().equals(ErrorCodes.NOT_FOUND)) {
                throw new AppException(e.getErrorCode(), url);
            } else {
                throw e;
            }
        } catch (HttpClientErrorException exception) {
            try {
                if (exception.getStatusCode().equals(HttpStatus.CONFLICT)) {
                    int errCode = Integer.parseInt(exception.getResponseHeaders().getFirst("Error-Code"));
                    String errorMsg = exception.getResponseHeaders().getFirst("Error-Message");
                    errorMsg = new String(errorMsg.getBytes("ISO-8859-1"), "GB2312");
                    log.warn("invoke remote call {} error. excode:{}, exmsg:{}", url, errCode, errorMsg);
                    throw new AppException(ErrorCodes.REST_FAIL, url, errorMsg);
                }
            } catch (AppException e) {
                throw e;
            } catch (Exception e) {
                //do nothing...
                e.printStackTrace();
                throw new AppException(ErrorCodes.REST_FAIL, url, e.getMessage());
            }
        } finally {
            log.debug("remote call {} cost:{} ms", url, System.currentTimeMillis() - startTime);
        }
        return result;
    }

    @Value("${cas.http.port}")
    private Integer casHttpPort;
    @Value("${cas.https.port}")
    private Integer casHttpsPort;

    private Integer casPortByResourcePlatform(String platform, String protocol, Integer port) {
        if (ReportResourceEnum.uis.name().equals(platform)) {
            if ("https".equals(protocol)) {
                port = casHttpsPort;
            } else {
                port = casHttpPort;
            }
        }
        return port;
    }

    public <T> T get(String platform, String host, String protocol, Integer port, String username, String password, String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(host, protocol,
                this.casPortByResourcePlatform(platform, protocol, port),
                username, password, url, HttpMethod.GET, HttpEntity.EMPTY, responseType, uriVariables);
    }

//    public <T> T put(String host, String protocol, String username, String password, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
//        return exchange(host, protocol, username, password, url, HttpMethod.PUT, requestEntity, responseType, uriVariables);
//    }

    public <T> T put(String platform, String host, String protocol, Integer port, String username, String password, String url, Object requestObj, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(host, protocol,
                this.casPortByResourcePlatform(platform, protocol, port),
                username, password, url, HttpMethod.PUT, Objects.isNull(requestObj) ? HttpEntity.EMPTY : new HttpEntity<>(SerializeUtils.toJson(requestObj)), responseType, uriVariables);
    }

    public <T> T post(String platform, String host, String protocol, Integer port, String username, String password, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(host, protocol,
                this.casPortByResourcePlatform(platform, protocol, port),
                username, password, url, HttpMethod.POST, requestEntity, responseType, uriVariables);
    }

    public <T> T delete(String platform, String host, String protocol, Integer port, String username, String password, String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return exchange(host, protocol,
                this.casPortByResourcePlatform(platform, protocol, port),
                username, password, url, HttpMethod.DELETE, HttpEntity.EMPTY, responseType, uriVariables);
    }

    /**
     * rest调用cas时添加x-forward参数
     *
     * @param requestEntity
     */
    private static HttpEntity<?> addRealIp(HttpEntity<?> requestEntity) {
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.addAll(requestEntity.getHeaders());
        String realIp = Objects.isNull(WebUtils.request()) ? "127.0.0.1" : WebUtils.realIp();
        newHeaders.set("x-forwarded-for", realIp);
        return new HttpEntity<>(requestEntity.getBody(), newHeaders);
    }

    private static void recordRequest(String url, HttpMethod method, HttpEntity<?> entity) {
        Object body = entity.getBody();
        String bodyInfo = InputStream.class.isInstance(entity) ? "<stream>" : SerializeUtils.toJson(body);
        String headerInfo = SerializeUtils.toJson(entity.getHeaders());
        log.debug("invoke cas method:{}, method:{}, header:{}, body:{}", url, method, headerInfo, bodyInfo);
    }
}
