package com.virtual.cloud.om.sdk.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by w17423 on 2019/3/6.
 */
@Slf4j
public class WebUtils {

    /**
     * 获取request
     *
     * @return
     */
    public static HttpServletRequest request() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getRequest).orElse(null);
    }

    public static HttpServletResponse response() {
        HttpServletResponse response =  Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getResponse).orElse(null);
        return response;
    }

    public  static Optional<Object> getRequestAttributeValue(String attributeKey) {
        return Optional.ofNullable(request()).map(request -> request.getAttribute(attributeKey));
    }

    /**
     * 获取session
     *
     * @return
     */
    public static HttpSession session() {
        return request().getSession();
    }



    @SneakyThrows
    public static String realIp() {
        return IpUtil.getIpAddr(request());
    }

    @SneakyThrows
    public static Locale currentLocale() {
        try{
            String lang = request().getHeader("lang");
            if (StringUtils.isNotBlank(lang) && !"null".equals(lang)) {
                switch (lang) {
                    case "en": {
                        return Locale.ENGLISH;
                    }
                    case "zh-CN": {
                        return Locale.CHINA;
                    }
                    default:return Locale.CHINA;
                }
            } else {
                return Locale.CHINA;
            }
        }catch (Exception e){
            return Locale.CHINA;
        }
    }
}
