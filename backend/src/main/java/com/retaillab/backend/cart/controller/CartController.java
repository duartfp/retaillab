package com.retaillab.backend.cart.controller;

import com.retaillab.backend.cart.dto.AddCartItemRequest;
import com.retaillab.backend.cart.dto.CartResponse;
import com.retaillab.backend.cart.dto.UpdateCartItemRequest;
import com.retaillab.backend.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart, current state of the (single, for now) cart.
     */
    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    /**
     * POST /api/cart/items, adds a product to the cart. If the product
     * is already in the cart, quantities are summed instead of duplicating
     * the line item.
     */
    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(request);
    }

    /**
     * PUT /api/cart/items/{itemId}, sets the quantity for an existing line item.
     */
    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItemQuantity(itemId, request.quantity());
    }

    /**
     * DELETE /api/cart/items/{itemId}, removes a line item entirely.
     */
    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(itemId);
    }
}
