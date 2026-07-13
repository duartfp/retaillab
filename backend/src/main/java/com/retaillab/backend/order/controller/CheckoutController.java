package com.retaillab.backend.order.controller;

import com.retaillab.backend.order.dto.OrderResponse;
import com.retaillab.backend.order.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    /**
     * POST /api/checkout, turns the current cart into an order (mocked
     * payment, goes straight to PAID) and empties the cart.
     */
    @PostMapping("/api/checkout")
    public OrderResponse checkout() {
        return checkoutService.checkout();
    }

    /**
     * GET /api/orders, order history for the authenticated user.
     */
    @GetMapping("/api/orders")
    public List<OrderResponse> listOrders() {
        return checkoutService.listOrdersForCurrentUser();
    }
}
