package com.simson.shopsphere.dto;

import com.simson.shopsphere.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {
    @NotNull(message = "Please select a delivery address")
    private Long addressId;

    @NotNull(message = "Please select a payment method")
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    private String couponCode;
    private String notes;

    public CheckoutRequest() {}

    public CheckoutRequest(Long addressId, PaymentMethod paymentMethod, String couponCode, String notes) {
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.couponCode = couponCode;
        this.notes = notes;
    }

    public static CheckoutRequestBuilder builder() {
        return new CheckoutRequestBuilder();
    }

    public static class CheckoutRequestBuilder {
        private Long addressId;
        private PaymentMethod paymentMethod = PaymentMethod.COD;
        private String couponCode;
        private String notes;

        public CheckoutRequestBuilder addressId(Long addressId) { this.addressId = addressId; return this; }
        public CheckoutRequestBuilder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public CheckoutRequestBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public CheckoutRequestBuilder notes(String notes) { this.notes = notes; return this; }

        public CheckoutRequest build() {
            return new CheckoutRequest(addressId, paymentMethod, couponCode, notes);
        }
    }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
