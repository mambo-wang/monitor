package com.virtual.cloud.om.agent.config.quartz;

import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author:XK
 * @Date:2022/4/27 10:34
 */
@Configuration
public class SchedulerConfiguration {

    @Resource
    private QuartzConfig quartzConfig;
    @Resource
    private MyJobFactory myJobFactory;
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        //获取配置属性
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setProperties(quartzConfig.quartzProperties());
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        //创建SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        Properties pro = propertiesFactoryBean.getObject();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setQuartzProperties(pro);
        factory.setJobFactory(myJobFactory);
        return factory;
    }

    /**
     * 初始化监听器
     * @return
     */
    @Bean
    public QuartzInitializerListener executorlistener(){
        return new QuartzInitializerListener();
    }


}
