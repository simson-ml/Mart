package com.simson.shopsphere.dto;

import com.simson.shopsphere.entity.OrderStatus;
import java.time.LocalDateTime;

public class OrderTimelineStepDto {
    private OrderStatus status;
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private boolean completed;
    private boolean current;

    public OrderTimelineStepDto() {}

    public OrderTimelineStepDto(OrderStatus status, String title, String description, LocalDateTime timestamp, boolean completed, boolean current) {
        this.status = status;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.completed = completed;
        this.current = current;
    }

    public static OrderTimelineStepDtoBuilder builder() {
        return new OrderTimelineStepDtoBuilder();
    }

    public static class OrderTimelineStepDtoBuilder {
        private OrderStatus status;
        private String title;
        private String description;
        private LocalDateTime timestamp;
        private boolean completed;
        private boolean current;

        public OrderTimelineStepDtoBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderTimelineStepDtoBuilder title(String title) { this.title = title; return this; }
        public OrderTimelineStepDtoBuilder description(String description) { this.description = description; return this; }
        public OrderTimelineStepDtoBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public OrderTimelineStepDtoBuilder completed(boolean completed) { this.completed = completed; return this; }
        public OrderTimelineStepDtoBuilder current(boolean current) { this.current = current; return this; }

        public OrderTimelineStepDto build() {
            return new OrderTimelineStepDto(status, title, description, timestamp, completed, current);
        }
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
}
