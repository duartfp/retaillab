package com.retaillab.backend.order.service;

import com.retaillab.backend.auth.model.User;
import com.retaillab.backend.auth.repository.UserRepository;
import com.retaillab.backend.cart.model.Cart;
import com.retaillab.backend.cart.model.CartItem;
import com.retaillab.backend.cart.repository.CartItemRepository;
import com.retaillab.backend.cart.service.CartService;
import com.retaillab.backend.catalog.model.Price;
import com.retaillab.backend.catalog.model.Product;
import com.retaillab.backend.order.dto.OrderItemResponse;
import com.retaillab.backend.order.dto.OrderResponse;
import com.retaillab.backend.order.model.Order;
import com.retaillab.backend.order.model.OrderItem;
import com.retaillab.backend.order.model.OrderStatus;
import com.retaillab.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Creates an order from whatever is currently in the cart, for the
     * authenticated user making the request, then empties the cart.
     * Payment is mocked, the order goes straight to PAID.
     */
    @Transactional
    public OrderResponse checkout() {
        Cart cart = cartService.getOrCreateCart();

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        User currentUser = currentUser();

        Order order = Order.builder()
                .user(currentUser)
                .status(OrderStatus.PAID)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        String currency = "BRL";

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            Price price = product.getCurrentPrice();

            if (price == null) {
                throw new IllegalStateException("Product " + product.getSku() + " has no active price");
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(price.getAmount())
                    .build();

            order.getItems().add(orderItem);
            total = total.add(price.getAmount().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            currency = price.getCurrency();
        }

        order.setTotal(total);
        order.setCurrency(currency);

        orderRepository.save(order);

        // Empty the cart now that it has been turned into an order.
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrdersForCurrentUser() {
        User currentUser = currentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductSku(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCurrency(),
                order.getCreatedAt(),
                items
        );
    }
}
