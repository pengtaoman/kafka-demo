package com.demo.entity;

public class Order {
    private Long shopId;
    private Long userId;
    private DeliveryType deliveryType;
    private boolean isCodPay;

    // shopId的getter和setter
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    // userId的getter和setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // deliveryType的getter和setter
    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    // isCodPay的getter和setter
    public boolean isCodPay() {
        return isCodPay;
    }

    public void setIsCodPay(boolean codPay) {
        isCodPay = codPay;
    }
}
