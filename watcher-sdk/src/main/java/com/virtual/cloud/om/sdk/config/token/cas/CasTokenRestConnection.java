package com.virtual.cloud.om.sdk.config.token.cas;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.sdk.api.LockApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestClient;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestClientCache;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.token.CasLoginEntityDTO;
import com.virtual.cloud.om.sdk.dto.token.ResourceHttpClientToken;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("casTokenRestConnection")
@ConditionalOnProperty(name = "rest-client.cas.enable", havingValue = "true")
public class CasTokenRestConnection {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private LockApi lockApi;


    public String refreshToken(String host, String protocol, int port, String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.cas.name()).and("host").is(host));
        ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
        if (Objects.nonNull(tokenDto) && StrUtil.isNotBlank(tokenDto.getToken())) {
            return tokenDto.getToken();
        }
        String key = String.format("refreshToken_%s_%s", host, ReportResourceEnum.cas.name());
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
        String token = "";
        if (StrUtil.isNotBlank(acquire)) {
            try {
                String tokenUrl = protocol + "://" + host + ":" + port + String.format(CasUriConstants.LOGIN, username, password);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                HttpEntity<CasLoginEntityDTO> requestEntity = new HttpEntity<>(null, headers);
                Optional<CasRestClient> restClient = CasRestClientCache.get().get(host, protocol, port, username, password);
                if (!restClient.isPresent()) {
                    throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR, tokenUrl);
                }
                //TODO:要换DTO
                ResponseEntity<String> responseEntity = restClient.get().getRestTemplate().exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);
                if (!Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")).isEmpty()) {
                    token = Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")).get(0).split(";")[0];
                }
                query = new Query();
                query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.cas.name())
                        .and("host").is(host));
                Update update = new Update();
                update.set("token", token);
                update.set("resource", ReportResourceEnum.cas.name());
                update.set("host", host);
                update.setOnInsert("createTime", DateUtil.now());
                update.set("updateTimeMs", System.currentTimeMillis());
                try {
                    this.mongoTemplate.upsert(query, update, ResourceHttpClientToken.class);
                } catch (Exception e) {
                    // 忽略
                }
            }finally {
                this.lockApi.release(key, acquire);
            }
        }
        return token;
    }

    public RestTemplate find(String ip, String protocol, int port, String username, String password) {
        Optional<CasTokenRestClient> restClient = CasTokenRestClientCache.get().get(ip, protocol, port, username, password);
        return restClient.<RestTemplate>map(CasTokenRestClient::getRestTemplate).orElse(null);
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
            } else if (e.getErrorCode().equals(ErrorCodes.UNAUTHORIZED)) {
                // 401 异常，需要重新获取token
                Query query = new Query();
                query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.workspace.name()).and("host").is(host));
                ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
                if (Objects.nonNull(tokenDto)) {
                    this.mongoTemplate.remove(tokenDto);
                }
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


    /**
     * 基础模板，直接套用restTemplate
     *
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
    public <T> ResponseEntity<T> exchange(String host, String protocol, String username, String password, int port, String url, HttpMethod method, HttpEntity<?> requestEntity,
                                          Class<T> responseType) {
        return exchangeResp(host, protocol, username, password, port, url, method, requestEntity, new ParameterizedTypeReference<T>() {
        });

    }

    public <T> ResponseEntity<T> exchange(String host, String protocol, String username, String password, int port, String url, HttpMethod method, HttpEntity<?> requestEntity,
                                          ParameterizedTypeReference<T> responseType) {
        return exchangeResp(host, protocol, username, password, port, url, method, requestEntity, responseType);
    }

    public String downloadBigFile(String host, String protocol, String username, String password, int port, String url, String targetPath, String fileName, String suffix) throws IOException {
        if (Files.notExists(Paths.get(targetPath))) {
            Files.createDirectories(Paths.get(targetPath));
        }
        String targetFilePath = targetPath + "/" + fileName + suffix;
        //如果文件已经存在，则删除文件
        File file = new File(targetFilePath);
        if (file.exists()) {
            file.delete();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, refreshToken(host, protocol, port, username, password));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        RequestCallback requestCallback = request -> request.getHeaders().putAll(headers);
        //restTemplate会把%转义为%25,所以用自己生成的uri
        URI uri = URI.create(accessUrl(url, host, protocol, port));

        RestTemplate restTemplate = find(host, protocol, port, username, password);
        restTemplate.execute(uri, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            FileCopyUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(targetFilePath));
            return targetFilePath;
        });
        return targetFilePath;
    }


    /**
     * get相关方法
     *
     * @param url
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return exchange(host, protocol, username, password, port, url, HttpMethod.GET, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<String> entity = buildHttpEntity(headers, null, username, password);
        return get(host, protocol, username, password, port, url, entity, responseType);
    }

    public <T> ResponseEntity<T> get(String host, String protocol, String username, String password, int port, String url, Class<T> responseType) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        headers.remove(HttpHeaders.CONTENT_TYPE);
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

    /**
     * post相关方法
     *
     * @param url
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
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

    /**
     * put相关方法
     *
     * @param url
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
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

    /**
     * delete相关方法
     *
     * @param url
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
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

    /**
     * patch相关方法
     *
     * @param url
     * @param requestEntity
     * @param responseType
     * @param <T>
     * @return
     */
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

    /**
     * 根据body构建通用HttpEntity
     *
     * @param body
     * @return
     */
    private HttpEntity<String> buildHttpEntityByBody(String body, String host, String protocol, int port, String username, String password) {
        HttpHeaders headers = commonHeader(host, protocol, port, username, password);
        if (StringUtils.isBlank(body)) {
            headers.remove(HttpHeaders.CONTENT_TYPE);
        }
        return buildHttpEntity(headers, body, username, password);
    }

    /**
     * 根据body与Header构建通用HttpEntity
     *
     * @param headers
     * @param body
     * @return
     */
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

    /**
     * 通用header构建
     *
     * @return
     */
    public HttpHeaders commonHeader(String host, String protocol, int port, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.COOKIE, refreshToken(host, protocol, port, username, password));
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
}
