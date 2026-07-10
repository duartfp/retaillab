package com.retaillab.backend.catalog.repository;

import com.retaillab.backend.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
