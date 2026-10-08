package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.exception.InvalidOrderStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Status Transition Validator Tests")
class OrderStatusTransitionValidatorTest {

    private OrderStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderStatusTransitionValidator();
    }

    @ParameterizedTest(name = "Valid transition from {0} to {1}")
    @CsvSource({
            "PLACED, CONFIRMED",
            "PLACED, CANCELLED",
            "CONFIRMED, PACKED",
            "CONFIRMED, CANCELLED",
            "PACKED, SHIPPED",
            "PACKED, CANCELLED",
            "SHIPPED, OUT_FOR_DELIVERY",
            "OUT_FOR_DELIVERY, DELIVERED",
            "DELIVERED, RETURN_REQUESTED",
            "RETURN_REQUESTED, RETURN_APPROVED",
            "RETURN_REQUESTED, RETURN_REJECTED",
            "RETURN_APPROVED, REFUNDED",
            "PLACED, PLACED",
            "DELIVERED, DELIVERED"
    })
    void shouldAllowValidTransitions(OrderStatus from, OrderStatus to) {
        assertThat(validator.isValidTransition(from, to)).isTrue();
        validator.validateTransition(from, to); // Should not throw
    }

    @ParameterizedTest(name = "Invalid transition from {0} to {1}")
    @CsvSource({
            "PLACED, DELIVERED",
            "PLACED, SHIPPED",
            "PLACED, OUT_FOR_DELIVERY",
            "SHIPPED, CANCELLED",
            "OUT_FOR_DELIVERY, CANCELLED",
            "DELIVERED, CANCELLED",
            "DELIVERED, PLACED",
            "CANCELLED, CONFIRMED",
            "REFUNDED, DELIVERED",
            "RETURN_REJECTED, REFUNDED"
    })
    void shouldRejectInvalidTransitions(OrderStatus from, OrderStatus to) {
        assertThat(validator.isValidTransition(from, to)).isFalse();
        assertThatThrownBy(() -> validator.validateTransition(from, to))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void shouldReturnAllowedNextStatusesCorrectly() {
        Set<OrderStatus> fromPlaced = validator.getAllowedNextStatuses(OrderStatus.PLACED);
        assertThat(fromPlaced).containsExactlyInAnyOrder(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);

        Set<OrderStatus> fromCancelled = validator.getAllowedNextStatuses(OrderStatus.CANCELLED);
        assertThat(fromCancelled).isEmpty();
    }

    @Test
    void shouldHandleNullGracefully() {
        assertThat(validator.isValidTransition(null, OrderStatus.PLACED)).isFalse();
        assertThat(validator.isValidTransition(OrderStatus.PLACED, null)).isFalse();
        assertThat(validator.getAllowedNextStatuses(null)).isEmpty();
    }
}
