package com.virtual.cloud.om.sdk.config.token.onestor;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtual.cloud.om.sdk.api.LockApi;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorClusterInfoDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OnestorLoginEntityDTO;
import com.virtual.cloud.om.sdk.dto.token.ResourceHttpClientToken;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OneStor REST连接 - 改造为内存缓存
 */
@Service("onestorRestConnection")
@Slf4j
@ConditionalOnProperty(name = "rest-client.onestor.enable", havingValue = "true")
public class OnestorRestConnection {

    // 内存缓存替代 MongoDB
    private final Map<String, ResourceHttpClientToken> tokenCache = new ConcurrentHashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private LockApi lockApi;

    private String getCacheKey(String resource, String host) {
        return resource + "_" + host;
    }

    public synchronized String refreshToken(String host, String protocol, int port, String username, String password) {
        String cacheKey = getCacheKey(ReportResourceEnum.onestor.name(), host);
        ResourceHttpClientToken cachedToken = tokenCache.get(cacheKey);
        log.info("resourcetoken is {}", cachedToken);
        if (Objects.nonNull(cachedToken) && StrUtil.isNotBlank(cachedToken.getToken())) {
            return cachedToken.getToken();
        }
        String key = String.format("refreshToken_%s_%s", host, ReportResourceEnum.onestor.name());
        String acquire = null;
        int tryLockNum = 0;
        final int maxTryLock = 10;
        while (true) {
            if ((++tryLockNum > maxTryLock) || StrUtil.isNotBlank(acquire = this.lockApi.acquire(key, TimeUnit.MILLISECONDS.toMinutes(1)))) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String cookieValue = "";
        if (StrUtil.isNotBlank(acquire)) {
            try {
                String tokenUrl = protocol + "://" + host + ":" + port + OnestoreUriConstants.auth.AUTH;
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
                OnestorLoginEntityDTO onestorLoginEntityDTO = new OnestorLoginEntityDTO();
                onestorLoginEntityDTO.setUsername(username);
                onestorLoginEntityDTO.setPassword(Base64Encoder.encode(password));
                String req = JSONUtil.toJsonStr(onestorLoginEntityDTO);
                HttpEntity<String> requestEntity = new HttpEntity<>(req, headers);
                ResponseEntity<Void> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.GET, requestEntity, Void.class);

                List<String> cookies = responseEntity.getHeaders().get("Set-Cookie");
                String token = cookies.stream().filter(v -> v.startsWith("XSRF-TOKEN")).findAny().orElse("");
                token = StringUtils.substringBefore(token, ";");
                String sessionId = cookies.stream().filter(v -> v.startsWith("calamari_sessionid")).findAny().orElse("");
                sessionId = StringUtils.substringBefore(sessionId, ";");
                cookieValue = sessionId + ";" + token;
                log.info("get login cookievalue is :{}", cookieValue);
                HttpHeaders headersPost = new HttpHeaders();
                headersPost.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
                headersPost.add("Cookie", cookieValue);
                String s = token.replace("XSRF-TOKEN=", "");
                headersPost.add("X-XSRF-TOKEN", s);
                HttpEntity<String> requestEntityPost = new HttpEntity<>(req, headersPost);
                ResponseEntity<Void> responseEntityPost = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntityPost, Void.class);
                List<String> cookiesPost = responseEntityPost.getHeaders().get("Set-Cookie");
                String tokenPost = cookiesPost.stream().filter(v -> v.startsWith("XSRF-TOKEN")).findAny().orElse("");
                token = StringUtils.substringBefore(tokenPost, ";");
                String sessionIdPost = cookiesPost.stream().filter(v -> v.startsWith("calamari_sessionid")).findAny().orElse("");
                sessionId = StringUtils.substringBefore(sessionIdPost, ";");
                cookieValue = sessionId + ";" + token;
                log.info("cookievalue is ---------:{}", cookieValue);
                
                // 保存到缓存
                ResourceHttpClientToken tokenDto = new ResourceHttpClientToken();
                tokenDto.setToken(cookieValue);
                tokenDto.setResource(ReportResourceEnum.onestor.name());
                tokenDto.setHost(host);
                tokenDto.setCreateTime(DateUtil.now());
                tokenDto.setUpdateTimeMs(System.currentTimeMillis());
                tokenCache.put(cacheKey, tokenDto);
            } catch (Exception e) {
                log.error("save resource token fail", e);
            } finally {
                this.lockApi.release(key, acquire);
            }
        }
        return cookieValue;
    }

    public RestTemplate find(String ip, String protocol, int port, String username, String password) {
        Optional<OnestorTokenRestClient> restClient = OnestorTokenRestClientCache.get().get(ip, protocol, port, username, password);
        return restClient.<RestTemplate>map(OnestorTokenRestClient::getRestTemplate).orElse(null);
    }

    public <T> ResponseEntity<T> exchangeResp(String host, String protocol, String username, String password, int port, String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        RestTemplate restTemplate = find(host, protocol, port, username, password);
        if (restTemplate == null) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        ResponseEntity<T> result = null;
        try {
            result = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
        } catch (AppException e) {
            if (e.getErrorCode().equals(ErrorCodes.NOT_FOUND)) {
                throw new AppException(e.getErrorCode(), url);
            } else if (e.getErrorCode().equals(ErrorCodes.UNAUTHORIZED) || e.getErrorCode().equals(ErrorCodes.FORBIDDEN)) {
                // 401,403 异常，需要重新获取token
                String cacheKey = getCacheKey(ReportResourceEnum.onestor.name(), host);
                tokenCache.remove(cacheKey);
                HttpHeaders headers = requestEntity.getHeaders();
                HttpHeaders newHeaders = new HttpHeaders();
                headers.entrySet().forEach(entry -> {
                    if (entry.getKey().equals(HttpHeaders.COOKIE)) {
                        newHeaders.add(entry.getKey(), this.refreshToken(host, protocol, port, username, password));
                    } else {
                        newHeaders.put(entry.getKey(), entry.getValue());
                    }
                });
                requestEntity = new HttpEntity<>(requestEntity.getBody(), newHeaders);
                result = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
            } else {
                throw e;
            }
        } catch (HttpClientErrorException exception) {
            try {
                if (exception.getStatusCode().equals(HttpStatus.CONFLICT)) {
                    int errCode = Integer.parseInt(exception.getResponseHeaders().getFirst("Error-Code"));
                    String errorMsg = exception.getResponseHeaders().getFirst("Error-Message");
                    log.warn("invoke remote call {} error. excode:{}, exmsg:{}", url, errCode, errorMsg);
                    throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR, url);
                }
            } catch (Exception e) {
                //do nothing...
            }
        } finally {
            log.debug("remote call {} cost:{} ms", url, System.currentTimeMillis() - startTime);
        }
        return result;
    }

    public <T> ResponseEntity<T> exchange(String host, String protocol, String username, String password, int port, String url, HttpMethod method, HttpEntity<?> requestEntity,
                                          Class<T> responseType) {
        return exchangeResp(host, protocol, username, password, port, url, method, requestEntity, new ParameterizedTypeReference<T>() {
        });

    }

    public <T> ResponseEntity<T> exchange(String host, String protocol, String username, String password, int port, String url, HttpMethod method, HttpEntity<?> requestEntity,
                                          ParameterizedTypeReference<T> responseType) {
        return exchangeResp(host, protocol, username, password, port, url, method, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.GET, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntity(headers, null, username, password);
        return get(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, Class<T> responseType) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        return get(host, protocol, username, password, port, url, headers, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.GET, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, ParameterizedTypeReference<T> responseType) {
        HttpEntity<String> entity = buildHttpEntity(headers, null, username, password);
        return get(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        return get(host, protocol, username, password, port, url, headers, responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.POST, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, Class<T> responseType) {
        return post(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, String body, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return post(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.POST, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, ParameterizedTypeReference<T> responseType) {
        return post(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, String username, String password, int port, String url, String body, ParameterizedTypeReference<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return post(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.PUT, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, Class<T> responseType) {
        return put(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, String body, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return put(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.PUT, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, ParameterizedTypeReference<T> responseType) {
        return put(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> put(String host, String protocol, String username, String password, int port, String url, String body, ParameterizedTypeReference<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return put(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.DELETE, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        HttpEntity<String> entity = buildHttpEntity(headers, null, username, password);
        return exchange(host, protocol, username, password, port, url, HttpMethod.DELETE, entity, responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, Class<T> responseType) {
        return delete(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, String body, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return delete(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.DELETE, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, ParameterizedTypeReference<T> responseType) {
        return delete(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> delete(String host, String protocol, String username, String password, int port, String url, String body, ParameterizedTypeReference<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return delete(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.PATCH, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, Class<T> responseType) {
        return patch(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, String body, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return patch(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.PATCH, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, String body, ParameterizedTypeReference<T> responseType) {
        return patch(host, protocol, username, password, port, url, new HttpEntity(body, headers), responseType);
    }

    public <T> ResponseEntity<T> patch(String host, String protocol, String username, String password, int port, String url, String body, ParameterizedTypeReference<T> responseType) {
        HttpEntity<String> entity = buildHttpEntityByBody(body, host, protocol, port, username, password);
        return patch(host, protocol, username, password, port, url, entity, responseType);
    }

    public String downloadBigFile(String host, String protocol, String username, String password, int port, String url, String targetPath, String fileName, String suffix) throws IOException {
        if (Files.notExists(Paths.get(targetPath))) {
            Files.createDirectories(Paths.get(targetPath));
        }
        String targetFilePath = targetPath + "/" + fileName + suffix;
        File file = new File(targetFilePath);
        if (file.exists()) {
            file.delete();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "AC_TOKEN=" + refreshToken(host, protocol, port, username, password));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        RequestCallback requestCallback = request -> request.getHeaders().putAll(headers);
        URI uri = URI.create(accessUrl(url, host, protocol, port));
        RestTemplate restTemplate = find(host, protocol, port, username, password);
        restTemplate.execute(uri, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            Files.copy(clientHttpResponse.getBody(), Paths.get(targetFilePath));
            return targetFilePath;
        });
        return targetFilePath;
    }

    private HttpEntity<String> buildHttpEntityByBody(String body, String host, String protocol, int port, String username, String password) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        return buildHttpEntity(headers, body, username, password);
    }

    public HttpEntity<String> buildHttpEntity(HttpHeaders headers, String body, String username, String password) {
        HttpHeaders newHeaders = new HttpHeaders();
        headers.forEach((name, value) -> {
            if (!CollectionUtils.isEmpty(value)) {
                newHeaders.put(name, value);
            }
        });

        HttpEntity<String> entity = StringUtils.isNotBlank(body) ?
                new HttpEntity<>(body, newHeaders) : new HttpEntity<>(newHeaders);
        return entity;
    }

    public HttpHeaders commonHeader(String host, String protocol, int port, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String cookieValue = refreshToken(host, protocol, port, username, password);
        String token = StringUtils.substringBefore(StringUtils.substringAfter(cookieValue, "XSRF-TOKEN="), ";");
        headers.add("Cookie", cookieValue);
        headers.add("X-XSRF-TOKEN", token);
        log.info("token is {},cookieValue is {},headers is {}", token, cookieValue, headers);
        return headers;
    }

    private String accessUrl(String uri, String host, String protocol, int port) {
        StringBuffer sb = new StringBuffer(protocol)
                .append("://")
                .append(host)
                .append(":")
                .append(port)
                .append(uri);
        return sb.toString();
    }

    public String getClusterId(String host, String protocol, String username, String password, Integer port) {
        String url = String.format(OnestoreUriConstants.Cluster.CLUSTER_INFO);
        OneStorRestResult oneStorRestResult = this.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>() {
        }).getBody();
        LinkedHashMap data = (LinkedHashMap) oneStorRestResult.getData();
        ObjectMapper mapper = new ObjectMapper();
        OneStorClusterInfoDTO oneStorClusterInfoDTO = mapper.convertValue(data, new TypeReference<OneStorClusterInfoDTO>() {
        });
        return oneStorClusterInfoDTO.getId();
    }
}
