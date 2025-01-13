package com.virtual.cloud.om.sdk.config.token.onestor;

import com.virtual.cloud.om.sdk.config.rest.VdiRestTemplate;
import com.virtual.cloud.om.sdk.config.rest.common.RestTemplateErrorHandler;
import com.virtual.cloud.om.sdk.config.rest.common.TrustAnyTrustManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
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
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * REST API客户端，目前仅支持摘要认证。
 */
public class OnestorTokenRestClient implements Closeable {

    /**
     * 登录客户端
     */
    private CloseableHttpClient client = null;

    /**
     * Spring 封装的进行Rest交互的模板工具类
     */
    private VdiRestTemplate restTemplate = null;

    /**
     * 日志
     */
    private Log log = LogFactory.getLog(getClass());

    public OnestorTokenRestClient(String protocol, String host, int port, String username,
                             String password) {
        this(protocol, host, port, username, password, 100, 100);
    }

    public OnestorTokenRestClient(String protocol, String host, int port, String username,
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
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .setSocketTimeout(12000000)
                .setConnectTimeout(12000000)
                .setConnectionRequestTimeout(5000)
                .build();

        client = HttpClients.custom()
                .setConnectionManager(ccm)
                .disableCookieManagement()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        /**添加LearningSpace全国大屏调用CAS Dash board接口所需HTTP消息转换器*/
        MappingJackson2HttpMessageConverter converterBigScreen = new MappingJackson2HttpMessageConverter();
        converterBigScreen.setSupportedMediaTypes(
                Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM}));

        restTemplate = new VdiRestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        restTemplate.getMessageConverters().add(new Jaxb2CollectionHttpMessageConverter());
        restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.getMessageConverters().add(converterBigScreen);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.setUriTemplateHandler(initUriTemplateHandler(protocol, host, port));
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        restTemplate.getInterceptors().add(new OneStorAcceptHeaderInterceptor());
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
                .setSoTimeout(12000)// 读取数据超时
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
        String baseUri = protocol + "://" + host + ":" + port;
        return new DefaultUriBuilderFactory(baseUri);
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
}
