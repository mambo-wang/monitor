package com.virtual.cloud.om.sdk.config.rest.workspace.cache;

import com.virtual.cloud.om.sdk.config.rest.VdiRestTemplate;
import com.virtual.cloud.om.sdk.config.rest.common.AcceptHeaderInterceptor;
import com.virtual.cloud.om.sdk.config.rest.common.RestTemplateErrorHandler;
import com.virtual.cloud.om.sdk.config.rest.common.TrustAnyTrustManager;
import com.virtual.cloud.om.sdk.dto.RestHost;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriTemplateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * REST API客户端，目前仅支持摘要认证。
 */
public class RestClient implements Closeable {

    /** 登录客户端 */
    private CloseableHttpClient client = null;

    /** Spring 封装的进行Rest交互的模板工具类 */
    private VdiRestTemplate restTemplate = null;

    /** 日志 */
    private Log log = LogFactory.getLog(getClass());

    public RestClient(RestHost config) {
        // maxTotalConnections 最大HTTP连接数
        // perRouteConnections 每个路由（即到某个目的地址）的最大HTTP连接数；建议与maxTotalConnections保持一致
        this(config, 100, 100);
    }

    public RestClient(RestHost config, int maxTotalConnections, final int perRouteConnections) {
        this(config.getProtocol(), config.getHost(), config.getPort(), config.getUsername(), config.getPassword(),
                maxTotalConnections, perRouteConnections);
    }

    public RestClient(String protocol, String host, int port, String username,
                      String password, int maxTotalConnections, final int perRouteConnections) {
        protocol = StringUtils.trimToNull(protocol);
        if (protocol == null) {
            throw new IllegalArgumentException("invalid protocol");
        }
        protocol = protocol.toLowerCase();
        host = StringUtils.trimToNull(host);
        if (host == null) {
            throw new IllegalArgumentException("invalid host");
        }
        username = StringUtils.trimToNull(username);

        //Client Pool
        PoolingHttpClientConnectionManager ccm = initConnectionPool(host, port, maxTotalConnections, perRouteConnections);

        // 认证信息
        CredentialsProvider credentialsProvider = initCredentialsProvider(host, port, username, password);

        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.DIGEST))
                // 客户端和服务器建立连接后，客户端从服务器读取数据的 timeout
                .setSocketTimeout(1000*60*60*2)//Be careful!设置不超时临时规避CAS和Broker的问题，后续考虑优化Broker 120000
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(1000*60*60*2)
                .build();

        client = HttpClients.custom()
                .setConnectionManager(ccm)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler((exception, executionCount, context) -> {
                    if (executionCount > 3){
                        return false;
                    }
                    if (exception instanceof NoHttpResponseException) {
                        return true;
                    }
                    return false;
                })
                .build();


        restTemplate = new VdiRestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        setRestTemplateEncode(restTemplate);
        restTemplate.getMessageConverters().add(new Jaxb2CollectionHttpMessageConverter());
        restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.setUriTemplateHandler(initUriTemplateHandler(protocol, host, port));
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        restTemplate.getInterceptors().add(new AcceptHeaderInterceptor());
    }

    private CredentialsProvider initCredentialsProvider(String host, int port, String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (username != null && password != null) {
            credentialsProvider.setCredentials(
                    new AuthScope(host, port, null),
                    new UsernamePasswordCredentials(username, password));
        }
        return credentialsProvider;
    }

    private PoolingHttpClientConnectionManager initConnectionPool(String host, int port, int maxTotalConnections, int perRouteConnections) {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(initSSLContext(), NoopHostnameVerifier.INSTANCE))
                .build();

        PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
//                .setSoTimeout(5000)// 读取数据超时
                .setSoReuseAddress(true)// 允许地址复用
                .setRcvBufSize(60000)// socket缓冲
                .setSndBufSize(60000)
                .build();
        ccm.setDefaultSocketConfig(socketConfig);
        ccm.setSocketConfig(new HttpHost(host, port), socketConfig);
        ccm.setMaxTotal(maxTotalConnections);
        ccm.setDefaultMaxPerRoute(perRouteConnections);
        ccm.setMaxPerRoute(new HttpRoute(new HttpHost(host, port)), perRouteConnections);
        return ccm;
    }

    private SSLContext initSSLContext() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);
            return sc;
        } catch (Exception e) {
            log.warn(null, e);
            throw new RuntimeException("Can not init SSLContext");
        }
    }

    private UriTemplateHandler initUriTemplateHandler(String protocol, String host, int port) {
        DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler();
        handler.setBaseUrl(protocol + "://" + host + ":" + port);
        return handler;
    }

    /**
     * 获取当前HttpClient对应的RestTemplate
     *
     * @return 当前HttpClient对应的RestTemplate
     */
    public VdiRestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * 退出时关闭。
     */
    @Override
    public void close() {
        try {
            this.client.close();
        } catch (IOException e) {
            log.warn("client close error", e);
        }
    }

    public static void setRestTemplateEncode(RestTemplate restTemplate) {
        List httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter){
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
    }
}
