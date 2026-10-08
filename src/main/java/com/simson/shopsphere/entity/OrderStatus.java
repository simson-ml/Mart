package com.simson.shopsphere.entity;

public enum OrderStatus {
    PLACED("Placed", "Order has been placed and received by store"),
    CONFIRMED("Confirmed", "Order verified and confirmed by seller"),
    PACKED("Packed", "Items packed and ready for dispatch"),
    SHIPPED("Shipped", "Order handed over to courier partner"),
    OUT_FOR_DELIVERY("Out for Delivery", "Package is out with local delivery agent"),
    DELIVERED("Delivered", "Package delivered successfully to customer"),
    CANCELLED("Cancelled", "Order was cancelled"),
    RETURN_REQUESTED("Return Requested", "Customer requested a return/replacement"),
    RETURN_APPROVED("Return Approved", "Return request approved by seller"),
    RETURN_REJECTED("Return Rejected", "Return request rejected"),
    REFUNDED("Refunded", "Order amount refunded to original payment method");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED || this == RETURN_REJECTED;
    }

    public boolean isCancellable() {
        return this == PLACED || this == CONFIRMED || this == PACKED;
    }

    public boolean isReturnable() {
        return this == DELIVERED;
    }
}
