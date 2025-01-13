package com.virtual.cloud.om.sdk.config.rest.workspace;

import com.virtual.cloud.om.sdk.config.rest.VdiRestTemplate;
import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.config.rest.workspace.cache.RestClient;
import com.virtual.cloud.om.sdk.config.rest.workspace.cache.RestClientCache;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

/**
 * REST连接获取MGR
 */
@Service("wsRestConnection")
@ConditionalOnProperty(name = "rest-client.ws.enable", havingValue = "true", matchIfMissing = true)
public class WsRestConnection {
    @Value("${vdi.ws.admin.username}")
    private String username;
    @Value("${vdi.ws.admin.password}")
    private String password;

    private static Logger log = LoggerFactory.getLogger(WsRestConnection.class);

    public WsRestConnection() {
    }

    public Optional<VdiRestTemplate> find(String host, String protocol, int port) {
        RestClient restClient = RestClientCache.get().get(host, protocol, port, username, password);
        if (restClient != null) {
            return Optional.ofNullable(restClient.getRestTemplate());
        }
        return Optional.empty();
    }

    public <T> T exchange(String host, String protocol, int port, String url, HttpMethod method, HttpEntity<?> requestEntity, RestType<T> responseType, Object... uriVariables) {
        VdiRestTemplate vdiRestTemplate = find(host, protocol, port).orElseThrow(() -> new AppException(ErrorCodes.REST_NOT_FOUND));
        ResponseEntity<T> exchange = vdiRestTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
        return exchange.getBody();
    }

    public <T> T get(String host, String protocol, int port, String url, RestType<T> responseType, Object... uriVariables) {
        return exchange(host, protocol, port, url, HttpMethod.GET, HttpEntity.EMPTY, responseType, uriVariables);
    }

    public <T> T put(String host, String protocol, int port, String url, Object requestObj, RestType<T> responseType) {
        return exchange(host, protocol, port, url, HttpMethod.PUT, new HttpEntity<>(SerializeUtils.toJson(requestObj)), responseType);
    }

    public <T> T post(String host, String protocol, int port, String url, Object requestObj, RestType<T> responseType) {
        return exchange(host, protocol, port, url, HttpMethod.POST, new HttpEntity<>(SerializeUtils.toJson(requestObj)), responseType);
    }

    public <T> ResponseEntity<T> post(String host, String protocol, int port, String url, HttpEntity<MultiValueMap<String, Object>> httpEntity, Class<T> responseType) {
        VdiRestTemplate vdiRestTemplate = find(host, protocol, port).orElseThrow(() -> new AppException(ErrorCodes.REST_NOT_FOUND));
        return vdiRestTemplate.postForEntity(url, httpEntity, responseType);
    }

    //返回文件下载的path
    public String downloadBigFile(String host, String protocol, int port, String url, String targetPath, String domainName, String suffix) throws IOException {
        String targetFilePath = targetPath + "/" + domainName + suffix;
        //如果文件已经存在，则删除文件
        File file = new File(targetFilePath);
        if (file.exists()) {
            file.delete();
        }
        VdiRestTemplate vdiRestTemplate = find(host, protocol, port).orElseThrow(() -> new AppException(ErrorCodes.REST_NOT_FOUND));
        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        vdiRestTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            Files.copy(clientHttpResponse.getBody(), Paths.get(targetFilePath));
            return targetFilePath;
        });
        return targetFilePath;
    }

    public <T> T delete(String host, String protocol, int port, String url, RestType<T> responseType, Object... uriVariables) {
        return exchange(host, protocol, port, url, HttpMethod.DELETE, HttpEntity.EMPTY, responseType, uriVariables);
    }

    public <T> T delete(String host, String protocol, int port, String url, Object requestObj, RestType<T> responseType, Object... uriVariables) {
        return exchange(host, protocol, port, url, HttpMethod.DELETE, new HttpEntity<>(SerializeUtils.toJson(requestObj)), responseType, uriVariables);
    }

    public void close(String host, String protocol, int port) {
        RestClient restClient = RestClientCache.get().get(host, protocol, port, username, password);
        if (restClient != null) {
            restClient.close();
        }
    }
}
