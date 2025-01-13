package com.virtual.cloud.om.config.quartz;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * @author:XK
 * @Date:2022/4/28 18:05
 */
@Data
@Component
public class QuartzConfig {

    @Value("${spring.data.mongodb.uri}")
    private  String mongodbUriAndDB;


    /**
     * 设置属性
     * @return
     * @throws IOException
     */
    public  Properties quartzProperties() throws IOException {
        String mongoUri = mongodbUriAndDB.substring(0, mongodbUriAndDB.lastIndexOf("/"));
        String mongoDB = mongodbUriAndDB.substring(mongodbUriAndDB.lastIndexOf("/")).replace("/","");
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "quartzInstanceName");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        prop.put("org.quartz.scheduler.jmx.export", "true");

        prop.put("org.quartz.jobStore.class", "com.novemberain.quartz.mongodb.MongoDBJobStore");
        prop.put("org.quartz.jobStore.mongoUri",mongoUri);
        prop.put("org.quartz.jobStore.dbName",mongoDB);
        //集群
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
//        此时间大于10000（10秒）会导致MISFIRE_INSTRUCTION_DO_NOTHING不起作用
        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "10");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        return prop;
    }


}
