package com.demo;

import com.demo.entity.BookInfo;
import com.demo.entity.DeliveryType;
import com.demo.entity.GoodsInfo;
import com.demo.entity.Order;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookExperiment {

    private KafkaMessageProducer producer;

    private ExecutorService es = Executors.newFixedThreadPool(10);


    // 模拟并发下单
    public void generateOrders() {
        for (int i=1; i < 1000; i++) {
            es.submit(() -> {
                book();
            });
        }
    }

    Random random = new Random(System.currentTimeMillis());

    private BookInfo book() {
        BookInfo bookInfo = new BookInfo();
        Order order = new Order();

        Long shopId = 654321L + random.nextInt(10000);
        Long userId = 1234L + random.nextInt(1000);
        Long goodsId = 5678L + random.nextInt(4000);
        order.setShopId(shopId);
        order.setUserId(userId);
        order.setDeliveryType(DeliveryType.express);
        order.setIsCodPay(false);
        bookInfo.setOrder(order);

        GoodsInfo goods = new GoodsInfo();
        goods.setGoodsId(goodsId);
        goods.setShopId(shopId);
        goods.setTitle("认养一头牛");
        goods.setDesc("2箱*250g");
        bookInfo.setGoods(goods);

        // 下单成功后发送消息
        producer.sendAsync(
                new ProducerRecord<>("order-events", bookInfo),
                (metadata, exception) -> callback(bookInfo, metadata, exception));

        return bookInfo;
    }


    // 消息发送后的回调函数
    private void callback(BookInfo bookInfo, RecordMetadata metadata, Exception ex) {
        if (metadata != null) {
            System.out.println("发送订单消息:" + bookInfo.getOrder().getShopId() + " 偏移量: " + metadata.offset() + " 主题: " + metadata.topic());
        } else {
            System.out.println("发送订单消息失败: " + ex.getMessage());
        }
    }
}
