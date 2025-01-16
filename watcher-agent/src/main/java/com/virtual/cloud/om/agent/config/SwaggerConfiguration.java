package com.virtual.cloud.om.agent.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @Author: w22798
 * @Date: 2022/4/26 16:20
 */
@Configuration
@EnableKnife4j
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    @Bean(value = "restApi")
    public Docket restApi(){

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("能力中心Agent接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virtual.cloud.om"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("swagger-bootstrap-ui RESTFul APIs")
                .description("<div style='font-size:14px;color:red;'>能力中心Agent-API文档</div>")
                .termsOfServiceUrl("http://localhost/")
                .contact(new Contact("NEW H3C", "www.h3c.com", "itservice@h3c.com"))
                .version("1.0")
                .build();
    }

}
