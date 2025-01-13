package com.virtual.cloud.om.sdk.config.rest.workspace.cache;

import com.virtual.cloud.om.sdk.dto.RestHost;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * REST客户端缓存。
 *
 * @author l10178
 */
public class RestClientCache {

    private static final RestClientCache instance = new RestClientCache();

    public static RestClientCache get() {
        return instance;
    }

    /** 每个VDI配置维护一套连接。 */
    private ConcurrentMap<String, RestClient> connections = new ConcurrentHashMap<>();

    public RestClient get(String wsIp, String protocol, int port, String username, String password) {
        String key = wsIp + protocol + port + username + password;
        RestClient restClient =  connections.get(key);
        if(Objects.isNull(restClient)){
            RestHost restHost = RestHost.builder()
                    .host(wsIp)
                    .port(port)
                    .protocol(protocol)
                    .username(username)
                    .password(password)
                    .build();
            restClient = new RestClient(restHost);
            connections.put(key, restClient);
        }
        return restClient;
    }

    public void shutdown(String wsIp) {
        RestClient client = connections.remove(wsIp);
        if(Objects.nonNull(client)){
            client.close();
        }
    }

    public void remove(String host){
        connections.remove(host);
    }

    public void shutdown() {
        connections.forEach((wsIp, client) -> client.close());
    }

    public void shutdown(RestClient client) {
        client.close();
    }

}
