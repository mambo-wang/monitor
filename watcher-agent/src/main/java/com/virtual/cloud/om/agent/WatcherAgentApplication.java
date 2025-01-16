package com.virtual.cloud.om.agent;

import com.spring4all.mongodb.EnableMongoPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author z13465 2022/4/14
 */
@SpringBootApplication(scanBasePackages = {"com.virtual.cloud.om.onestor","com.virtual.cloud.om.sdk","com.virtual.cloud.om.cas", "com.virtual.cloud.om.uis", "com.virtual.cloud.om.self", "com.virtual.cloud.om.workspace", "com.virtual.cloud.om.log","com.virtual.cloud.om.agent"})
@EnableScheduling
@EnableMongoPlus
public class WatcherAgentApplication {

    public static String[] args;
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        WatcherAgentApplication.args = args;
        WatcherAgentApplication.context = SpringApplication.run(WatcherAgentApplication.class, args);
    }

}
