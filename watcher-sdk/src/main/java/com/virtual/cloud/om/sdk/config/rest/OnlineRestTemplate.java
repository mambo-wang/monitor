package com.virtual.cloud.om.sdk.config.rest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.SpringProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.*;
import org.springframework.web.util.UriTemplateHandler;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/10/26 15:53
 */
@Component
@Slf4j
public class OnlineRestTemplate extends RestTemplate {

    public OnlineRestTemplate(ClientHttpRequestFactory requestFactory) {
        super();
        setRequestFactory(requestFactory);
    }
    @Nullable
    @Override
    @SneakyThrows
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
                              @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
        if (!url.toString().contains("/oad-center/watcher/download/")){
            return super.doExecute(url,method,requestCallback,responseExtractor);
        }else {
            log.info("=====MyRestTemplate=======");
            Assert.notNull(url, "URI is required");
            Assert.notNull(method, "HttpMethod is required");
            ClientHttpResponse response = null;
            try {
                ClientHttpRequest request = createRequest(url, method);
                if (requestCallback != null) {
                    requestCallback.doWithRequest(request);
                }
                response = request.execute();
                handleResponse(url, method, response);
                return (responseExtractor != null ? responseExtractor.extractData(response) : null);
            }
            catch (IOException ex) {
                String resource = url.toString();
                String query = url.getRawQuery();
                resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
                if (ex instanceof SSLException){
                    throw new SSLException("connect reset ",ex);
                }
                throw new ResourceAccessException("I/O error on " + method.name() +
                        " request for \"" + resource + "\": " + ex.getMessage(), ex);
            }
            finally {
                if (response != null) {
                    response.close();
                }
            }
        }

    }
}
