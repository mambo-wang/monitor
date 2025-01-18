package com.virtual.cloud.om.performer.clickhouse;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.command.CommandSettings;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.clickhouse.client.api.internal.ServerSettings;
import com.clickhouse.client.api.query.QueryResponse;
import com.virtual.cloud.om.performer.clickhouse.data.PojoWithJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExperimentalJSONExample {

    @Autowired
    private Client client;

    final String tableName = "pojo_with_json_table";
    final String createSQL = PojoWithJSON.createTable(tableName);
    final String originalJsonStr = "{\"a\":{\"b\":\"42\"},\"c\":[\"1\",\"2\",\"3\"]}";


    public void writeData() {
        CommandSettings commandSettings = new CommandSettings();
        commandSettings.serverSetting("allow_experimental_json_type", "1");

        try {
            client.execute("DROP TABLE IF EXISTS " + tableName, commandSettings).get(1, TimeUnit.SECONDS);
            client.execute(createSQL, commandSettings).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        client.register(PojoWithJSON.class, client.getTableSchema(tableName, "default"));
        PojoWithJSON pojo = new PojoWithJSON();
        pojo.setEventPayload(originalJsonStr);
        List<Object> data = Arrays.asList(pojo);

        InsertSettings insertSettings = new InsertSettings()
                .serverSetting(ServerSettings.INPUT_FORMAT_BINARY_READ_JSON_AS_STRING, "1");
        try (InsertResponse response = client.insert(tableName, data, insertSettings).get(30, TimeUnit.SECONDS)) {
            log.info("Data write metrics: {}", response.getMetrics());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String readData() {
        try (QueryResponse resp = client.query("SELECT * FROM " + tableName).get(1, TimeUnit.SECONDS)) {
            ClickHouseBinaryFormatReader reader = client.newBinaryFormatReader(resp);
            assert reader.next() != null;
            String jsonStr = reader.getString(1);
            log.info("Read JSON string: {}", jsonStr);
            return jsonStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
