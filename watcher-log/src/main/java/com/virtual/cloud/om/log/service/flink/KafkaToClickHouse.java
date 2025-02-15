package com.virtual.cloud.om.log.service.flink;

import com.virtual.cloud.om.log.entity.OperationLog;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class KafkaToClickHouse {

    public static void main(String[] args) throws Exception {
        // 设置执行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 配置 Kafka 消费者的属性
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092"); // Kafka 服务器地址
        properties.setProperty("group.id", "testGroup"); // 消费者组
        
        // 创建 Kafka 消费者
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
            "my_topic", // Kafka 主题
            new SimpleStringSchema(),  // 消息解码器
            properties); // 属性配置

        // 将消费者添加至环境
        // 在 main 方法中的原有代码后面
        env.addSource(consumer).print();
//        env.addSource(consumer)
//                .map(KafkaToClickHouse::processData) // 处理每条 Kafka 消息
//                .addSink((dataModel) -> writeToClickHouse(dataModel)); // 写入 ClickHouse
        env.execute("Flink Kafka to ClickHouse");
    }

    public static OperationLog processData (String jsonData){
        OperationLog operationLog = SerializeUtils.json2Object(jsonData, OperationLog.class);
        return operationLog;
    }

    public static void writeToClickHouse(OperationLog dataModel) {
        String url = "jdbc:clickhouse://localhost:8123";
        String user = "default";
        String password = "";

        String insertQuery = "INSERT INTO your_table (field1, field2) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, dataModel.getResult());
            preparedStatement.setString(2, dataModel.getDesc());
            preparedStatement.executeUpdate(); // 执行更新
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}