package com.virtual.cloud.om.sdk.config.rest.uis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.token.ResourceHttpClientToken;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by y17381 on 2019/10/29.
 */
@Slf4j
@Service("uisRestConnection")
@ConditionalOnProperty(name = "rest-client.uis.enable", havingValue = "true", matchIfMissing = false)
public class UisRestConnection {
    @Autowired
    private RestTemplate restTemplate;


    @Value("${vdi.uis.admin.username}")
    private String username;

    @Value("${vdi.uis.admin.password}")
    private String password;

    @Value("${vdi.provider.operator.port:6060}")
    private int tokenPort;


    @Resource
    private MongoTemplate mongoTemplate;

    public String refreshToken(String host) {
        Query query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name()).and("host").is(host));
        ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
        if (Objects.nonNull(tokenDto) && StrUtil.isNotBlank(tokenDto.getToken())) {
            return tokenDto.getToken();
        }
        String tokenUrl = "http" + "://" + host + ":" + tokenPort + UisUriConstants.Oauth.OAUTH_TOKEN;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "23");
        params.add("username", username);
        params.add("password", password);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<UisTokenResult> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, UisTokenResult.class);

        String token = responseEntity.getBody().getAccessToken();
        query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name())
                .and("host").is(host).and("updateTimeMs").lt(System.currentTimeMillis() - (60 * 1000)));
        Update update = new Update();
        update.set("token",token);
        update.set("resource",ReportResourceEnum.uis.name());
        update.set("host",host);
        update.set("createTime", DateUtil.now());
        update.set("updateTimeMs",System.currentTimeMillis());
        try{
            this.mongoTemplate.upsert(query,update,ResourceHttpClientToken.class);
        }catch (Exception e){
            // 忽略
        }
        return token;
    }

    public RestTemplate find(String ip, String protocol, int port, String username, String password) {
        Optional<UisTokenRestClient> restClient = UisTokenRestClientCache.get().get(ip, protocol, port, username, password);
        return restClient.<RestTemplate>map(UisTokenRestClient::getRestTemplate).orElse(null);
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
            updateUisJsessionId(host, result);
        } catch (AppException e) {
            if (e.getErrorCode().equals(ErrorCodes.NOT_FOUND)) {
                throw new AppException(e.getErrorCode(), url);
            } else {
                throw e;
            }
        } catch (HttpClientErrorException exception) {
            log.error("[uis-client] error happened, code is {}", exception.getStatusCode());
            if (exception.getStatusCode().equals(HttpStatus.CONFLICT)) {
                int errCode = Integer.parseInt(exception.getResponseHeaders().getFirst("Error-Code"));
                String errorMsg = exception.getResponseHeaders().getFirst("Error-Message");
                log.warn("[uis-client] invoke remote call {} error. excode:{}, exmsg:{}", url, errCode, errorMsg);
                throw new AppException(ErrorCodes.REST_FAIL, url, errorMsg);
            }

            if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                log.error("[uis-client] uis request error :{}, class :{}", exception.getMessage(), exception.getClass().toString());
                HttpEntity<?> httpEntity = refreshHttpEntity(requestEntity, host,protocol,port,username,password);
                return restTemplate.exchange(url, method, httpEntity, responseType, uriVariables);
            }
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.error("[uis-client] uis request error :{}, class :{}", exception.getMessage(), exception.getClass().toString());
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
        } finally {
            log.debug("remote call {} cost:{} ms", url, System.currentTimeMillis() - startTime);
        }
        return result;
    }

    private HttpEntity<?> refreshHttpEntity(HttpEntity<?> requestEntity, String host,String protocol, int port, String username, String password) {
        //todo 删除已有token
        Query query = new Query();
        query = query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name())
                .and("host").is(host));
        mongoTemplate.remove(query,ResourceHttpClientToken.class);
        HttpHeaders headers = commonHeader(host,protocol,port,username,password);
        if (Objects.isNull(requestEntity.getBody())) {
            headers.remove(HttpHeaders.CONTENT_TYPE);
        }

        HttpHeaders newHeaders = new HttpHeaders();
        headers.forEach((name, value) -> {
            if (!CollectionUtils.isEmpty(value)) {
                newHeaders.put(name, value);
            }
        });

        return Objects.isNull(requestEntity.getBody()) ?
                new HttpEntity<>(newHeaders) : new HttpEntity<>(requestEntity.getBody(), newHeaders);
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
        Query query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name()).and("host").is(host));
        ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
        String cookie = "";
        if (ObjectUtils.isNotEmpty(tokenDto) && StringUtils.isNotEmpty(tokenDto.getSessionId())) {
            cookie = tokenDto.getSessionId() + ";AC_TOKEN=" + refreshToken(host);
        } else {
            cookie = "AC_TOKEN=" + refreshToken(host);
        }
        log.info("set uis-cookie = " + cookie);
        headers.add(HttpHeaders.COOKIE, cookie);
//        headers.add(HttpHeaders.COOKIE, "AC_TOKEN=" + refreshToken(host,protocol,port,username,password));
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

    public String downloadBigFile(String host, String protocol, String username, String password, int port, String url, String targetPath, String fileName, String suffix) throws IOException {
        if(Files.notExists(Paths.get(targetPath))){
            Files.createDirectories(Paths.get(targetPath));
        }
        String targetFilePath = targetPath + "/" + fileName + suffix ;
        //如果文件已经存在，则删除文件
        File file = new File(targetFilePath);
        if (file.exists()) {
            file.delete();
        }
        HttpHeaders headers = new HttpHeaders();
        Query query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name()).and("host").is(host));
        ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
        String cookie = "";
        if (StringUtils.isNotEmpty(tokenDto.getSessionId())) {
            cookie = tokenDto.getSessionId() + ";AC_TOKEN=" + refreshToken(host);
        } else {
            cookie = "AC_TOKEN=" + refreshToken(host);
        }
        log.info("set uis-cookie = " + cookie);
        headers.add(HttpHeaders.COOKIE, cookie);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        RequestCallback requestCallback = request -> request.getHeaders().putAll(headers);
        //restTemplate会把%转义为%25,所以用自己生成的uri
        URI uri = URI.create(accessUrl(url, host, protocol, port));
        RestTemplate restTemplate = find(host, protocol, port, username, password);
        restTemplate.execute(uri, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            Files.copy(clientHttpResponse.getBody(), Paths.get(targetFilePath));
            return targetFilePath;
        });
        return targetFilePath;
    }

    private void updateUisJsessionId(String host, ResponseEntity responseEntity){
        Query query = new Query();
        query.addCriteria(Criteria.where("resource").is(ReportResourceEnum.uis.name()).and("host").is(host));
        ResourceHttpClientToken tokenDto = this.mongoTemplate.findOne(query, ResourceHttpClientToken.class);
        Update update = new Update();
        String token = "";
        if (Objects.nonNull(tokenDto) && StrUtil.isNotBlank(tokenDto.getToken())) {
            token = tokenDto.getToken();
        }
        HttpHeaders headers = responseEntity.getHeaders();
        /**获取UIS JSESSIONID*/
        List<String> cookies = headers.get("Set-Cookie");
        if (!CollectionUtils.isEmpty(cookies)) {
            Optional<String> jsessionid = cookies.stream().filter(s -> s.contains("JSESSIONID")).findAny();
            if (jsessionid.isPresent()){
                String[] split = jsessionid.get().split(";");
                String s = split[0];
                log.info("update uis-cookie JSESSIONID : " + s);
                update.set("sessionId",s);
                this.mongoTemplate.upsert(query,update,ResourceHttpClientToken.class);
            }
        }
    }
}
