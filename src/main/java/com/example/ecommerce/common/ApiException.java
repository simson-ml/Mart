package com.example.ecommerce.common;

public class ApiException extends RuntimeException {
    public ApiException(String m) {
        super(m);
    }
}
