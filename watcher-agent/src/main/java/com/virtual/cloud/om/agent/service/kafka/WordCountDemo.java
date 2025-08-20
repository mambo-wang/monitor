package com.virtual.cloud.om.agent.service.kafka;


import com.virtual.cloud.om.sdk.dto.FileBeatRaw;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public final class WordCountDemo {
    public static void main(final String[] args) {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-stream-demo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.99.224.74:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        final StreamsBuilder builder = new StreamsBuilder();


        final KStream<String, String> source = builder.stream("topic-filebeat-receiver");


        source.mapValues(WordCountDemo::map);

        source.to("wordcount-output-topic");


        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);


        Runtime.getRuntime().addShutdownHook(new Thread("wordcount-stream-demo-jvm-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });


        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static String map(String value){
        FileBeatRaw fileBeatRaw = SerializeUtils.json2Object(value, FileBeatRaw.class);
        String message = fileBeatRaw.getMessage();
        fileBeatRaw.setMessage("mapped value");
        return SerializeUtils.toJson(fileBeatRaw);
    }
}
