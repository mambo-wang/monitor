package com.virtual.cloud.om.sdk.config.clickhouse;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.internal.ServerSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "clickhouse", name = "enable", havingValue = "true", matchIfMissing = false)
public class ClickHouseClientConfig {

    @Value("${chEndpoint}")
    private String endpoint;
    @Value("${chUser}")
    private String user;
    @Value("${chPassword}")
    private String password;
    @Value("${chDatabase}")
    private String hDatabase;

    @Bean
    public Client client(){

        Client client = new Client.Builder()
                .addEndpoint(endpoint)
                .setUsername(user)
                .setPassword(password)
                .setDefaultDatabase(hDatabase)
                .compressServerResponse(false)
                .compressClientRequest(false)
                .setLZ4UncompressedBufferSize(1048576)
                .useNewImplementation(true)
                // when network buffer and socket buffer are the same size - it is less IO calls and more efficient
                .setSocketRcvbuf(1_000_000)
                .setClientNetworkBufferSize(1_000_000)
                .setMaxConnections(20)
                .serverSetting(ServerSettings.INPUT_FORMAT_BINARY_READ_JSON_AS_STRING, "1")
                .serverSetting(ServerSettings.OUTPUT_FORMAT_BINARY_WRITE_JSON_AS_STRING, "1")
                .serverSetting("allow_experimental_json_type", "1")
                .build();

        return client;
    }
}
