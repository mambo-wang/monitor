package com.virtual.cloud.om.performer.controller;


import com.clickhouse.client.api.Client;
import com.virtual.cloud.om.performer.PerformerServiceApplication;
import com.virtual.cloud.om.performer.clickhouse.*;
import com.virtual.cloud.om.sdk.config.clickhouse.POJO2DbWriter;
import com.virtual.cloud.om.sdk.config.clickhouse.SimpleReader;
import com.virtual.cloud.om.sdk.config.clickhouse.Stream2DbWriter;
import com.virtual.cloud.om.sdk.config.clickhouse.TextFormatsReader;
import com.virtual.cloud.om.sdk.entity.clickhouse.ArticleViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
public class ClickhouseController {

    @Autowired
    private Client clickhouseClient;

    @Autowired
    private Stream2DbWriter stream2DbWriter;

    @Autowired
    private SimpleReader simpleReader;

    @Autowired
    private TextFormatsReader textFormatsReader;

    @Autowired
    private POJO2DbWriter pojo2DbWriter;

    @Autowired
    private ExperimentalJSONExample experimentalJSONExample;


    @GetMapping("/insert")
    public String test(){

        stream2DbWriter.resetTable();

        log.info("Inserting data from resources/sample_hacker_news_posts.json");
        try (InputStream is = PerformerServiceApplication.class.getResourceAsStream("/sample_hacker_news_posts.json")) {
            stream2DbWriter.insertData_JSONEachRowFormat(is);
        } catch (Exception e) {
            log.error("Failed to insert data", e);
        }

        // Read data back
        simpleReader.readDataUsingBinaryFormat();
        //
        simpleReader.readDataAll();
        simpleReader.readData();

        // Read as Text format
        textFormatsReader.readAsJsonEachRow();
        textFormatsReader.readAsJsonEachRowButGSon();
        textFormatsReader.readJSONEachRowIntoArrayOfObject();
        textFormatsReader.readJSONEachRowIntoArrayOfObjectGson();
        textFormatsReader.readAsCSV();
        textFormatsReader.readAsTSV();

        // Insert data using POJO
        pojo2DbWriter.resetTable();
        for (int i = 0; i < 10; i++) {
            pojo2DbWriter.submit(new ArticleViewEvent(11132929d, LocalDateTime.now(), UUID.randomUUID().toString()));
        }

        pojo2DbWriter.printLastEvents();

        // Insert data using POJO with JSON
        experimentalJSONExample.writeData();
        return experimentalJSONExample.readData();
    }

}
