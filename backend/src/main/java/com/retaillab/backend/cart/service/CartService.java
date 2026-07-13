package com.retaillab.backend.cart.service;

import com.retaillab.backend.auth.model.User;
import com.retaillab.backend.auth.repository.UserRepository;
import com.retaillab.backend.cart.dto.AddCartItemRequest;
import com.retaillab.backend.cart.dto.CartItemResponse;
import com.retaillab.backend.cart.dto.CartResponse;
import com.retaillab.backend.cart.model.Cart;
import com.retaillab.backend.cart.model.CartItem;
import com.retaillab.backend.cart.repository.CartItemRepository;
import com.retaillab.backend.cart.repository.CartRepository;
import com.retaillab.backend.catalog.model.Price;
import com.retaillab.backend.catalog.model.Product;
import com.retaillab.backend.catalog.repository.ProductRepository;
import com.retaillab.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Returns the authenticated user's cart, creating one if this is
     * their first interaction with it. Every cart endpoint requires a
     * valid JWT (see SecurityConfig), so an authenticated principal is
     * always expected to be present here.
     */
    @Transactional
    public Cart getOrCreateCart() {
        User currentUser = currentUser();
        return cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(currentUser).build()));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
    }

    @Transactional
    public CartResponse getCart() {
        return toResponse(getOrCreateCart());
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found, id " + request.productId()));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.quantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(request.quantity())
                        .build());

        cartItemRepository.save(item);
        return toResponse(getOrCreateCart());
    }

    @Transactional
    public CartResponse updateItemQuantity(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found, id " + itemId));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return toResponse(getOrCreateCart());
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Cart item not found, id " + itemId);
        }
        cartItemRepository.deleteById(itemId);
        return toResponse(getOrCreateCart());
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(item -> item.subtotal())
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        String currency = itemResponses.stream()
                .map(item -> item.currency())
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse("BRL");

        return new CartResponse(cart.getId(), itemResponses, total, currency);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        Price price = product.getCurrentPrice();

        BigDecimal unitPrice = price != null ? price.getAmount() : null;
        BigDecimal subtotal = unitPrice != null
                ? unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
                : null;

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSku(),
                item.getQuantity(),
                unitPrice,
                subtotal,
                price != null ? price.getCurrency() : null
        );
    }
}
