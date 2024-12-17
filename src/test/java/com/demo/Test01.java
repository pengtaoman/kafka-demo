package com.demo;


import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test01 {

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
}
