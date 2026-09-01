package com.example.ecommerce.common;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    Map<String, String> handle(ApiException e) {
        return Map.of("error", e.getMessage());
    }
}
