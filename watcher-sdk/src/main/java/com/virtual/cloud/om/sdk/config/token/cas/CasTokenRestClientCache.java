package com.virtual.cloud.om.sdk.config.token.cas;

import com.virtual.cloud.om.sdk.config.token.PublicCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * REST客户端缓存。
 *
 * @author zkf9688
 */
public class CasTokenRestClientCache {

    private static Logger log = LoggerFactory.getLogger(CasTokenRestClientCache.class);

    private static final CasTokenRestClientCache instance = new CasTokenRestClientCache();

    public static CasTokenRestClientCache get() {
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
    private ConcurrentMap<String, CasTokenRestClient> connections = new ConcurrentHashMap<>();

    public Optional<CasTokenRestClient> get(String url, String protocol, int port, String username, String password) {
        String key = url+protocol+port+username+password;
        if(connections.get(key)==null){
            PublicCloud cloud = new PublicCloud();
            cloud.setProtocal(protocol);
            cloud.setUri(url);
            cloud.setPort(port);
            cloud.setUsername(username);
            cloud.setPassword(password);
            CasTokenRestClient client = new CasTokenRestClient(cloud.getProtocal(), cloud.getUri(), cloud.getPort(), cloud.getUsername(), cloud.getPassword(), maxTotalConnections, perRouteConnections);
            connections.put(key, client);
            return Optional.of(client);
        }
        return Optional.of(connections.get(key));
    }

    public void shutdown() {
        connections.forEach((cloudId, client) -> client.close());
    }

    public void shutdown(CasTokenRestClient client) {
        client.close();
        log.info("restClient closed");
    }

    public void remove(String host){
        connections.remove(host);
    }
}
