package com.virtual.cloud.om.agent.config;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.utils.CookieUtil;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.agent.service.auth.LoginService;
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
        // 优先从请求头获取token，其次从Cookie获取
        String token = request.getHeader("token");
        log.info("[LoginInterceptor] path: {}, token from header: {}", request.getRequestURI(), token);
        if (StringUtils.isEmpty(token)) {
            Cookie cookie = CookieUtil.readCookie(request, Constant.TOKEN_NAME);
            log.info("[LoginInterceptor] cookie: {}", cookie);
            if (Objects.isNull(cookie)) {
                RpcResult result = new RpcResult();
                result.setState(StateResult.FAILURE);
                result.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
                result.setFailureMessage(sm.getString("token.error"));
                this.response(response, result);
                return false;
            }
            token = cookie.getValue();
        }
        
        RpcResult result = new RpcResult();
        if (StringUtils.isEmpty(token)){
            result.setState(StateResult.FAILURE);
            result.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            result.setFailureMessage(sm.getString("token.error"));
            this.response(response,result);
            return false;
        }
        Boolean verify = loginService.verify(token);
        log.info("[LoginInterceptor] verify result: {}", verify);
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
