package com.demo;

import com.alibaba.fastjson.JSONObject;
import com.demo.entity.BookInfo;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;

import java.util.Properties;

public class KafkaMessageStream {

    public void init() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "orderCount");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String,String> source = streamBuilder.stream("order-events");

        // 计算下单中每个 goodsId 出现的次数
        KStream result = source.filter(
                (key, value) -> value.startsWith("{") && value.endsWith("}")
        ).mapValues(
                value -> JSONObject.parseObject(value, BookInfo.class)
        ).mapValues(
                bookInfo -> bookInfo.getGoods().getGoodsId().toString()
        ).groupBy((key,value) -> value).count(Materialized.as("goods-order-count")
        ).mapValues(value -> Long.toString(value)).toStream();

        result.print(Printed.toSysOut());

        new Thread(
                () -> {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    KafkaStreams streams = new KafkaStreams(streamBuilder.build(), properties);
                    streams.start();
                    System.out.println("stream-start ...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    streams.close();
                }
        ).start();
    }
}
