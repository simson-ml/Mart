package com.simson.shopsphere.service;

import com.simson.shopsphere.dto.ReviewRequest;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.Review;
import com.simson.shopsphere.entity.Role;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.repository.ReviewRepository;
import com.simson.shopsphere.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User buyer;
    private User otherUser;
    private Product product;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .id(201L)
                .name("Alice Buyer")
                .email("alice@shopsphere.com")
                .role(Role.USER)
                .build();

        otherUser = User.builder()
                .id(202L)
                .name("Bob Other")
                .email("bob@shopsphere.com")
                .role(Role.USER)
                .build();

        product = Product.builder()
                .id(301L)
                .name("Wireless Noise Canceling Headphones")
                .price(BigDecimal.valueOf(5000.00))
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Review submission without title succeeds for verified buyer")
    void testSubmitReviewWithoutTitleSucceeds() {
        ReviewRequest request = new ReviewRequest(301L, 5, "", "Amazing sound quality and fast delivery!");

        when(productRepository.findById(301L)).thenReturn(Optional.of(product));
        when(reviewRepository.hasPurchasedAndDelivered(buyer, product)).thenReturn(true);
        when(reviewRepository.findByProductAndUser(product, buyer)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reviewRepository.calculateAverageRating(product)).thenReturn(5.0);
        when(reviewRepository.countByProduct(product)).thenReturn(1L);

        Review saved = reviewService.submitReview(buyer, request);

        assertNotNull(saved);
        assertEquals(5, saved.getRating());
        assertEquals("Amazing sound quality and fast delivery!", saved.getComment());
        assertTrue(saved.isVerifiedPurchase());
        verify(productRepository, times(1)).save(product);
        assertEquals(new BigDecimal("5.00"), product.getRating());
        assertEquals(1, product.getReviewCount());
    }

    @Test
    @DisplayName("Unverified buyer cannot submit review")
    void testUnverifiedBuyerCannotSubmitReview() {
        ReviewRequest request = new ReviewRequest(301L, 4, null, "Haven't received it yet");

        when(productRepository.findById(301L)).thenReturn(Optional.of(product));
        when(reviewRepository.hasPurchasedAndDelivered(buyer, product)).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                reviewService.submitReview(buyer, request)
        );

        assertTrue(ex.getMessage().contains("only submit reviews for products you have purchased and received"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Non-author cannot delete another user's review")
    void testNonAuthorCannotDeleteReview() {
        Review review = Review.builder()
                .id(901L)
                .user(buyer)
                .product(product)
                .rating(5)
                .comment("Great!")
                .build();

        when(reviewRepository.findById(901L)).thenReturn(Optional.of(review));

        assertThrows(BadRequestException.class, () ->
                reviewService.deleteReview(901L, otherUser)
        );

        verify(reviewRepository, never()).delete(any(Review.class));
    }
}
