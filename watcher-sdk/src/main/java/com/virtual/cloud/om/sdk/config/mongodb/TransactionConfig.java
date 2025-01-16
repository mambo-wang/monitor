package com.virtual.cloud.om.sdk.config.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * @author SYSTEM
 */
@Configuration
@ConditionalOnProperty(prefix = "mongodb", name = "enable", havingValue = "true", matchIfMissing = false)
public class TransactionConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUriAndDB;


    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory){

        return new MongoTransactionManager(factory);
    }

}
