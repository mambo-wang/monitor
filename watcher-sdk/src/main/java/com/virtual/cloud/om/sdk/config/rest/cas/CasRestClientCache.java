package com.virtual.cloud.om.sdk.config.rest.cas;

import com.virtual.cloud.om.sdk.dto.RestHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * REST客户端缓存。
 *
 * @author l10178
 */
public class CasRestClientCache {

    private static Logger log = LoggerFactory.getLogger(CasRestClientCache.class);

    private static final CasRestClientCache instance = new CasRestClientCache();

    public static CasRestClientCache get() {
        return instance;
    }

    /**
     * 最大HTTP连接数
     */
    private final static int maxTotalConnections = 100;

    /**
     * 每个路由（即到某个目的地址）的最大HTTP连接数；建议与maxTotalConnections保持一致
     */
    private final static int perRouteConnections = 100;

    /**
     * 每个VDI配置维护一套连接。
     */
    private ConcurrentMap<String, CasRestClient> connections = new ConcurrentHashMap<>();

    public Optional<CasRestClient> get(String ip, String protocol, int port, String username, String password) {
        String key = ip+protocol+port+username+password;
        if(connections.get(key)==null){

            RestHost cloud = new RestHost();
            cloud.setProtocol(protocol);
            cloud.setHost(ip);
            cloud.setPort(port);
            cloud.setUsername(username);
            cloud.setPassword(password);
            CasRestClient client = new CasRestClient(cloud.getProtocol(), cloud.getHost(), cloud.getPort(), cloud.getUsername(), cloud.getPassword(), maxTotalConnections, perRouteConnections);
            connections.put(key, client);
            return Optional.of(client);
        }
        return Optional.of(connections.get(key));
    }

    public void shutdown() {
        connections.forEach((cloudId, client) -> client.close());
    }

    public void shutdown(CasRestClient client) {
        client.close();
        log.info("restClient closed");

    }

    public void remove(String host){
        connections.remove(host);
    }

}
