package com.virtual.cloud.om;

import com.spring4all.mongodb.EnableMongoPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author z13465 2022/4/14
 */
@SpringBootApplication
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
