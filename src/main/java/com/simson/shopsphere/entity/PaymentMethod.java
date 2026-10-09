package com.simson.shopsphere.entity;

public enum PaymentMethod {
    COD("Cash on Delivery"),
    UPI("UPI / QR Code"),
    ONLINE_MOCK("Online Payment (Mock UPI / Card / NetBanking)");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
