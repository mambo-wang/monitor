package com.virtual.cloud.om.performer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author: w22798
 * @Date: 2025/1/16 6:16
 */
@SpringBootApplication(scanBasePackages = {"com.virtual.cloud.om.sdk", "com.virtual.cloud.om.performer"})
public class PerformerServiceApplication {

    public static String[] args;
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        PerformerServiceApplication.args = args;
        PerformerServiceApplication.context = SpringApplication.run(PerformerServiceApplication.class, args);
    }
}
