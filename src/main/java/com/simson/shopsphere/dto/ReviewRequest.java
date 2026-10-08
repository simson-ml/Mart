package com.simson.shopsphere.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating = 5;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;

    public ReviewRequest() {}

    public ReviewRequest(Long productId, Integer rating, String title, String comment) {
        this.productId = productId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
    }

    public static ReviewRequestBuilder builder() {
        return new ReviewRequestBuilder();
    }

    public static class ReviewRequestBuilder {
        private Long productId;
        private Integer rating = 5;
        private String title;
        private String comment;

        public ReviewRequestBuilder productId(Long productId) { this.productId = productId; return this; }
        public ReviewRequestBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewRequestBuilder title(String title) { this.title = title; return this; }
        public ReviewRequestBuilder comment(String comment) { this.comment = comment; return this; }

        public ReviewRequest build() {
            return new ReviewRequest(productId, rating, title, comment);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
