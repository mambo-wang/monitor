package com.virtual.cloud.om.config;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.utils.CookieUtil;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.service.auth.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/4 16:33
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    private static StringManager sm = StringManager.getManager("Auth");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie cookie = CookieUtil.readCookie(request, Constant.TOKEN_NAME);
        RpcResult result = new RpcResult();
        if (Objects.isNull(cookie)) {
            result.setState(StateResult.FAILURE);
            result.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            result.setFailureMessage(sm.getString("token.error"));
            this.response(response,result);
            return false;
        }
        String token = cookie.getValue();
        if (StringUtils.isEmpty(token)){
            result.setState(StateResult.FAILURE);
            result.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            result.setFailureMessage(sm.getString("token.error"));
            this.response(response,result);
            return false;
        }
        Boolean verify = loginService.verify(token);
        if (!verify){
            result.setState(StateResult.FAILURE);
            result.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            result.setFailureMessage(sm.getString("token.error"));
            this.response(response,result);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void  response(HttpServletResponse response, RpcResult res){
        try {
            response.setContentType("application/json;charset=utf-8");
            response.sendError(HttpStatus.HTTP_UNAUTHORIZED);
            PrintWriter out = response.getWriter();

            out.write(JSONUtil.toJsonPrettyStr(res));
            out.flush();
            out.close();
        }catch (Exception e){

        }

    }
}
