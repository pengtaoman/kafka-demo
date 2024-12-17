package com.demo.entity;

public class BookInfo {
    private Order order;
    private GoodsInfo goods;

    // Order相关的getter和setter
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // GoodsInfo相关的getter和setter
    public GoodsInfo getGoods() {
        return goods;
    }

    public void setGoods(GoodsInfo goods) {
        this.goods = goods;
    }
}