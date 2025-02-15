package com.virtual.cloud.om.log.service.flink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlinkConfig {
    @Value("${flink.parallelism:8}")
    private int parallelism;

    @Bean
    public StreamExecutionEnvironment flinkExecutionEnvironment() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);
        return env;
    }
}