package com.virtual.cloud.om.performer.config;
 
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
@Component
@ConfigurationProperties(prefix = "spring.datasource.click")
public class ConnectionParamConfig {
 
    private String driverClassName ;
    private String url ;
    private Integer initialSize ;
    private Integer maxActive ;
    private Integer minIdle ;
    private Integer maxWait ;
 
    private String username;
    private String password;
 
    public String getDriverClassName() {
        return driverClassName;
    }
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Integer getInitialSize() {
        return initialSize;
    }
    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }
    public Integer getMaxActive() {
        return maxActive;
    }
    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }
    public Integer getMinIdle() {
        return minIdle;
    }
    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }
    public Integer getMaxWait() {
        return maxWait;
    }
    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
}
 
 