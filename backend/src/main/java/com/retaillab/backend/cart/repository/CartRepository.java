package com.retaillab.backend.cart.repository;

import com.retaillab.backend.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
