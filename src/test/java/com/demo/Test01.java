package com.demo;


import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.consumer.ConsumerConfig;

public class Test01 {

    private static final Logger logger = Logger.getLogger(Test01.class.getName());

    @BeforeAll
    public static void beforeAll() {

        logger.setLevel(Level.INFO);
    }

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
// 创建一个CompletableFuture
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 模拟长时间的计算任务
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello, ";
        });

        // 当future完成时，使用thenApply来处理结果并返回一个新的CompletableFuture
        CompletableFuture<String> future2 = future.thenApply(greeting -> greeting + "World!");

        // 当future2完成时，使用thenAccept来消费结果
        future2.thenAccept(System.out::println);

        Thread.sleep(4000);
        // 如果你需要在计算完成后得到最终结果，可以调用get方法
        // 注意：get()方法会阻塞直到future完成
//        String result = future2.get();
//        System.out.println("Final result: " + result);
    }


    @Test
    void produceMsg() throws ExecutionException, InterruptedException {
        KafkaMessageProducer kpd = new KafkaMessageProducer();
        kpd.init();
        // 创建 ProducerRecord，表示一条消息
        String topic = "my-topic1";

        for (int i = 0; i < 10000; i++) {
            String key = "key" + i;
            String value = "Hello Kafka!-----" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            kpd.send(record);
            Thread.sleep(100);
        }

    }

    @Test
    void consumeMsg2() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.211.55.71:9092");  // Kafka Broker 地址
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");  // 消费者组 ID
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 消息键的反序列化器
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 消息值的反序列化器
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 将 key 的字节数组转成 Java 对象
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // 将 value 的字节数组转成 Java 对象
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // 如果没有偏移量，自动从最早的消息开始消费


        // 手动指定分区，假设 my-topic 主题有 3 个分区
        TopicPartition partition0 = new TopicPartition("my-topic1", 0);
        TopicPartition partition1 = new TopicPartition("my-topic1", 1);
        TopicPartition partition2 = new TopicPartition("my-topic1", 2);


        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        kafkaConsumer.subscribe(Collections.singleton("my-topic1"));  // 订阅主题 order-events

        // 通过 assign 指定消费者消费的分区
//        kafkaConsumer.assign(Arrays.asList(partition0, partition1, partition2));

        // 持续消费消息
        while (true) {
            // 拉取消息
            ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            // 处理消息
            records.forEach(record -> {
                System.out.println("Received message: ");
                System.out.println("Key: " + record.key());
                System.out.println("Value: " + record.value());
                System.out.println("Topic: " + record.topic());
                System.out.println("Partition: " + record.partition());
                System.out.println("Offset: " + record.offset());
                System.out.println("------------------------ ############################# ############################");
            });
        }
    }


    @Test
    void consumeMsg4() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.211.55.71:9092");  // Kafka Broker 地址

        /*
        * 消费者组名称一致，使用同一个topic，则是争抢模式
        * 消费者组名称不一致，使用同一个topic，则是发布订阅模式
        * */
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group009");  // 消费者组 ID
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 消息键的反序列化器
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 消息值的反序列化器
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 将 key 的字节数组转成 Java 对象
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  // 将 value 的字节数组转成 Java 对象
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // 如果没有偏移量，自动从最早的消息开始消费

        // 手动指定分区，假设 my-topic 主题有 3 个分区
        TopicPartition partition0 = new TopicPartition("my-topic1", 0);
        TopicPartition partition1 = new TopicPartition("my-topic1", 1);
        TopicPartition partition2 = new TopicPartition("my-topic1", 2);


        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        /*
        * subscribe 是kafka自动分配分区
        * */
        kafkaConsumer.subscribe(Collections.singleton("my-topic1"));  // 订阅主题 order-events

        // 通过 assign 指定消费者消费的分区
//        kafkaConsumer.assign(Arrays.asList(partition0, partition1, partition2));

        // 持续消费消息
        while (true) {
            // 拉取消息
            ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            // 处理消息
            records.forEach(record -> {
                System.out.println("Received message: ");
                System.out.println("Key: " + record.key());
                System.out.println("Value: " + record.value());
                System.out.println("Topic: " + record.topic());
                System.out.println("Partition: " + record.partition());
                System.out.println("Offset: " + record.offset());
                System.out.println("------------------------ ############################# ############################");
            });
        }
    }
}



