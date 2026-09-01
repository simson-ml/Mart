package com.example.ecommerce.address;

import jakarta.persistence.*;
import com.example.ecommerce.user.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String name, phone, address, city, state, pincode;
    private boolean defaultAddress;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User x) {
        user = x;
    }

    public String getName() {
        return name;
    }

    public void setName(String x) {
        name = x;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String x) {
        phone = x;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String x) {
        address = x;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String x) {
        city = x;
    }

    public String getState() {
        return state;
    }

    public void setState(String x) {
        state = x;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String x) {
        pincode = x;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean x) {
        defaultAddress = x;
    }
}
