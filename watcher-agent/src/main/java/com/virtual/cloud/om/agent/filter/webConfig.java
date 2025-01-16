package com.virtual.cloud.om.agent.filter;


import com.virtual.cloud.om.agent.config.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class webConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns( "/user/login",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**",
                        "/kafka/**",
                        "/collect/**",
                        "/log/**",
                        "/deploy/refresh",
                        "/deploy/host",
                        "/deploy/localIps",
                        "/deploy/keepalived/notify/**",
                        "/swagger-ui.html/**",
                        "/doc.html");
    }
}
