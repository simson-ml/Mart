package com.simson.shopsphere.exception;

import com.simson.shopsphere.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return uri.startsWith("/api/") ||
               "XMLHttpRequest".equalsIgnoreCase(requestedWith) ||
               (accept != null && accept.contains("application/json"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        log.warn("Resource not found: {} on URI {}", ex.getMessage(), request.getRequestURI());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
        }
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(InsufficientStockException.class)
    public Object handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("Insufficient stock error: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @ExceptionHandler(InvalidCouponException.class)
    public Object handleInvalidCoupon(InvalidCouponException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("Invalid coupon applied: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Object handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("Duplicate resource conflict: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public Object handleUnauthorizedAccess(UnauthorizedAccessException ex, HttpServletRequest request, Model model) {
        log.warn("Unauthorized access attempt: {} on URI {}", ex.getMessage(), request.getRequestURI());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
        }
        model.addAttribute("errorCode", 403);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed. Please correct input fields.")
                .data(errors)
                .build());
    }

    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String firstError = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Invalid form input data");

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(firstError));
        }
        redirectAttributes.addFlashAttribute("errorMessage", firstError);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled application error on URI: " + request.getRequestURI(), ex);
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
        }
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "Something went wrong on our end. Please try again later.");
        return "error/500";
    }
}
