package com.virtual.cloud.om.log.service.flink;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

//$bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ip_count_source
//$bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ip_count_sink

@Component
public class ExactlyOnceIpCount {

    @PostConstruct
    public static void init() throws Exception {

        // 设置输入和输出
        FlinkKafkaConsumer<IpAndCount> sourceConsumer = setupSource();

        // 设置运行时环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); // 按照EventTime来统计
        env.enableCheckpointing(5000); // 每5秒保存一次CheckPoint
        // 设置CheckPoint
        CheckpointConfig config = env.getCheckpointConfig();
        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // 设置CheckPoint模式为EXACTLY_ONCE
        config.enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION); // 取消任务时保留CheckPoint
        config.enableApproximateLocalRecovery(true); // 启动时从CheckPoint恢复任务

        // 设置CheckPoint的StateBackend，在这里CheckPoint保存在本地临时目录中。
        // 只适合单节点做实验，在生产环境应该使用分布式文件系统，例如HDFS。
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        env.setStateBackend((StateBackend) new FsStateBackend(tmpDirFile.toURI().toURL().toString()));
        // 设置故障恢复策略：任务失败的时候自动每隔10秒重启，一共尝试重启3次
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, // number of restart attempts
                10000 // delay
        ));

        // 定义输入：从Kafka中获取数据
        DataStreamSource<IpAndCount> input = env
                .addSource(sourceConsumer);

        // 计算：每5秒钟按照ip对count求和
        DataStream<IpAndCount> output =
                input
                .keyBy(IpAndCount::getIp) // 按照ip地址统计
                .window(TumblingEventTimeWindows.of(Time.seconds(5))) // 每5秒钟统计一次
                .allowedLateness(Time.seconds(5))
                .sum("count"); // 对count字段求和

        // 输出到kafka topic
        output.addSink(new LogSink());

        // execute program
        env.execute("Exactly-once IpCount");
    }

    private static FlinkKafkaConsumer<IpAndCount> setupSource() {
        // 设置Kafka Consumer属性
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.99.234.15:9092");
        properties.setProperty("group.id", "IpCount");

        // 创建 FlinkKafkaConsumer
        FlinkKafkaConsumer<IpAndCount> sourceConsumer =
                new FlinkKafkaConsumer<>("ip_count_source",
                        new AbstractDeserializationSchema<IpAndCount>() {
            // 自定义反序列化消息的方法：将非结构化的以空格分隔的文本直接转成结构化数据IpAndCount
            @Override
            public IpAndCount deserialize(byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                String [] splt = str.split("\\s");
                try {
                    return new IpAndCount(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(splt[0]),splt[1], 1L);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }, properties);

        // 告诉Flink时间从哪个字段中获取
        sourceConsumer.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<IpAndCount>() {
            @Override
            public long extractAscendingTimestamp(IpAndCount ipAndCount) {
                return ipAndCount.getDate().getTime();
            }

        });
        return sourceConsumer;
    }

}
