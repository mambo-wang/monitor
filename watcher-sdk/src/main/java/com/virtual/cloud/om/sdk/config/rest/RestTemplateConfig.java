package com.virtual.cloud.om.sdk.config.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtual.cloud.om.sdk.config.rest.common.RestTemplateErrorHandler;
import com.virtual.cloud.om.sdk.config.rest.common.TrustAnyTrustManager;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import java.net.SocketException;

/**
 * Created by x19765 on 2020/11/17.
 */
@Configuration
public class RestTemplateConfig {

    static {
        // JDK 17 不再内置 JAXB 实现，需要指定 Glassfish JAXB Runtime 作为 ContextFactory
        System.setProperty("jakarta.xml.bind.context.factory", "com.sun.xml.bind.v2.ContextFactory");
    }

    private Log log = LogFactory.getLog(getClass());

    @SneakyThrows
    @Bean
    public HttpClient httpClient(){
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                // 客户端和服务器建立连接后，客户端从服务器读取数据的 timeout
                .setSocketTimeout(1000*60*60*4)
                // 从连接池获取连接的 timeout
                .setConnectTimeout(1000*20)
                // 客户端和服务器建立连接的 timeout
                .setConnectionRequestTimeout(1000*10)
                .build();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https",  new SSLConnectionSocketFactory(initSSLContext(), NoopHostnameVerifier.INSTANCE))
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoTimeout(5*1000*60*60)// 读取数据超时
                .setSoReuseAddress(true)// 允许地址复用
                .setRcvBufSize(60000*60)// socket缓冲
                .setSndBufSize(60000*60)
                .build();

        PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);
        ccm.setMaxTotal(10000);
        ccm.setDefaultMaxPerRoute(10000);
        ccm.setDefaultSocketConfig(socketConfig);
//        ccm.setSocketConfig(new HttpHost(host, port), socketConfig);

        return HttpClients.custom()//HttpClientBuilder.create()
                .setDefaultRequestConfig(defaultRequestConfig).disableCookieManagement()
                .setConnectionManager(ccm)
                .setRetryHandler((exception, executionCount, context) -> {
                    if (executionCount > 10){
                        log.error("maximum tries reached for client http pool");
                        return false;
                    }
                    if (exception instanceof SocketException) {
                        log.error("SocketException on " + executionCount + " call");
                        return true;
                    }
                    if (exception instanceof NoHttpResponseException) {
                        log.error("NoHttpResponseException on " + executionCount + " call");
                        return true;
                    }
                    if (exception instanceof SSLException){
                        log.error("SSLException on " + executionCount + " call");
                        return true;
                    }
                    return false;
                })
                .build();
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

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(){
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        restTemplate.getMessageConverters().removeIf(converter-> MappingJackson2HttpMessageConverter.class.isInstance(converter));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter(objectMapper));
        restTemplate.getMessageConverters().add(new Jaxb2CollectionHttpMessageConverter());
        restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(OnlineRestTemplate.class)
    public OnlineRestTemplate myRestTemplate(ObjectMapper objectMapper) {
        OnlineRestTemplate restTemplate = new OnlineRestTemplate(httpRequestFactory());
        restTemplate.getMessageConverters().removeIf(converter-> MappingJackson2HttpMessageConverter.class.isInstance(converter));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter(objectMapper));
        restTemplate.getMessageConverters().add(new Jaxb2CollectionHttpMessageConverter());
        restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }
}
