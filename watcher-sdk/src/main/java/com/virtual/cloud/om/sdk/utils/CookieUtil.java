package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

public class CookieUtil {
    /**
     * 添加cookie
     * @param response
     * @param path
     * @param name
     * @param value
     * @param maxAge
     * @param httpOnly
     */
    public static void addCookie(HttpServletResponse response, String path,
                                 String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);

    }

    /**
     * 根据cookie名称读取cookie
     *
     * @param request
     * @return map<cookieName, cookieValue>
     */

    public static Map<String, String> readCookie(HttpServletRequest request, String... cookieNames) {
        Map<String, String> cookieMap = new HashMap<String, String>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                for (int i = 0; i < cookieNames.length; i++) {
                    if (cookieNames[i].equals(cookieName)) {
                        cookieMap.put(cookieName, cookieValue);
                    }
                }
            }
        }
        return cookieMap;
    }

    /**
     * 根据cookie名称读取cookie
     *
     * @param request
     * @return Cookie
     */

    public static Cookie readCookie(HttpServletRequest request, String cookieNames) {
        Cookie[] cookies = request.getCookies();
        if (!Objects.isNull(cookies)) {
            Cookie tokenCookie = Arrays.stream(cookies).filter(cookie -> StringUtils.equals(Constant.TOKEN_NAME, cookie.getName()))
                    .findFirst().orElse(null);
            return tokenCookie;
        }
        return null;
    }

    /**
     * 根据cookie名称删除cookie
     *
     * @param request
     * @return map<cookieName, cookieValue>
     */
    public static void delCookie(HttpServletRequest request,HttpServletResponse response, String cookieName) {
        List<Cookie> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }
        List<Cookie> collect = cookies.stream().filter(c -> c.getName().equals(cookieName)).collect(Collectors.toList());
        collect.forEach(cookie -> {
            cookie.setMaxAge(Constant.COOKIE_EXPIRED);
            cookie.setValue("");
            cookie.setPath(Constant.ACCESS_PATH);
            response.addCookie(cookie);
        });
    }


}