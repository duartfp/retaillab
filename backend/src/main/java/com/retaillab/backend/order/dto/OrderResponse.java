package com.retaillab.backend.order.dto;

import com.retaillab.backend.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        String currency,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
