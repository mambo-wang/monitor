package com.virtual.cloud.om.performer.config;
 
import javax.annotation.Resource;
 
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import javax.sql.DataSource;
 
@Configuration
public class DruidConfig {
 
    @Resource
    private ConnectionParamConfig jdbcParamConfig;
 
    /**
     * 重写 DataSource
     * @return
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(jdbcParamConfig.getUrl());
        datasource.setDriverClassName(jdbcParamConfig.getDriverClassName());
        datasource.setInitialSize(jdbcParamConfig.getInitialSize());
        datasource.setMinIdle(jdbcParamConfig.getMinIdle());
        datasource.setMaxActive(jdbcParamConfig.getMaxActive());
        datasource.setMaxWait(jdbcParamConfig.getMaxWait());
        datasource.setUsername(jdbcParamConfig.getUsername());
        datasource.setPassword(jdbcParamConfig.getPassword());
        return datasource;
    }
 
}
 
 
 