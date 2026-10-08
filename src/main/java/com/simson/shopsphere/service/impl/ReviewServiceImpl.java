package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.ReviewRequest;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Review;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.repository.ReviewRepository;
import com.simson.shopsphere.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReviewServiceImpl.class);

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }


    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Review submitReview(User user, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!canUserReview(user, product)) {
            throw new BadRequestException("You can only submit reviews for products you have purchased and received.");
        }

        Optional<Review> existingOpt = reviewRepository.findByProductAndUser(product, user);
        Review review;

        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment().trim());
        } else {
            review = Review.builder()
                    .product(product)
                    .user(user)
                    .rating(request.getRating())
                    .comment(request.getComment().trim())
                    .verifiedPurchase(true)
                    .build();
        }

        Review saved = reviewRepository.save(review);
        recalculateProductRating(product);

        log.info("Review saved for product {} by user {}", product.getName(), user.getEmail());
        return saved;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new BadRequestException("Unauthorized to delete this review.");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        recalculateProductRating(product);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserReview(User user, Product product) {
        if (user == null || product == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        return reviewRepository.hasPurchasedAndDelivered(user, product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getProductReviews(Product product, Pageable pageable) {
        return reviewRepository.findByProductOrderByCreatedAtDesc(product, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getProductReviewsList(Product product) {
        return reviewRepository.findByProductOrderByCreatedAtDesc(product);
    }

    private void recalculateProductRating(Product product) {
        Double avgRating = reviewRepository.calculateAverageRating(product);
        long count = reviewRepository.countByProduct(product);

        product.setReviewCount((int) count);
        if (avgRating != null) {
            product.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            product.setRating(BigDecimal.ZERO);
        }
        productRepository.save(product);
    }
}
