package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ReviewRequest;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Review;
import com.simson.shopsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    Review submitReview(User user, ReviewRequest request);
    void deleteReview(Long reviewId, User user);
    boolean canUserReview(User user, Product product);
    Page<Review> getProductReviews(Product product, Pageable pageable);
    List<Review> getProductReviewsList(Product product);
}
