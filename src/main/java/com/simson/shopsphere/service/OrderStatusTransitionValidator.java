package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.OrderStatus;
import com.simson.shopsphere.exception.InvalidOrderStatusException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderStatusTransitionValidator {

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        // PLACED can move to CONFIRMED or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.PLACED, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));

        // CONFIRMED can move to PACKED or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PACKED, OrderStatus.CANCELLED));

        // PACKED can move to SHIPPED or CANCELLED
        VALID_TRANSITIONS.put(OrderStatus.PACKED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));

        // SHIPPED can move to OUT_FOR_DELIVERY
        VALID_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.OUT_FOR_DELIVERY));

        // OUT_FOR_DELIVERY can move to DELIVERED
        VALID_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED));

        // DELIVERED can move to RETURN_REQUESTED
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.of(OrderStatus.RETURN_REQUESTED));

        // RETURN_REQUESTED can move to RETURN_APPROVED or RETURN_REJECTED
        VALID_TRANSITIONS.put(OrderStatus.RETURN_REQUESTED, EnumSet.of(OrderStatus.RETURN_APPROVED, OrderStatus.RETURN_REJECTED));

        // RETURN_APPROVED can move to REFUNDED
        VALID_TRANSITIONS.put(OrderStatus.RETURN_APPROVED, EnumSet.of(OrderStatus.REFUNDED));

        // Terminal states with no outgoing transitions
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, Collections.emptySet());
        VALID_TRANSITIONS.put(OrderStatus.REFUNDED, Collections.emptySet());
        VALID_TRANSITIONS.put(OrderStatus.RETURN_REJECTED, Collections.emptySet());
    }

    public boolean isValidTransition(OrderStatus from, OrderStatus to) {
        if (from == null || to == null) {
            return false;
        }
        if (from == to) {
            return true;
        }
        Set<OrderStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public void validateTransition(OrderStatus from, OrderStatus to) {
        if (!isValidTransition(from, to)) {
            throw new InvalidOrderStatusException(
                    String.format("Invalid order status transition from '%s' to '%s'.",
                            from != null ? from.getDisplayName() : "null",
                            to != null ? to.getDisplayName() : "null")
            );
        }
    }

    public Set<OrderStatus> getAllowedNextStatuses(OrderStatus current) {
        if (current == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(VALID_TRANSITIONS.getOrDefault(current, Collections.emptySet()));
    }
}
