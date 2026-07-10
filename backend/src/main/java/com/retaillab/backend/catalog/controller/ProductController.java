package com.retaillab.backend.catalog.controller;

import com.retaillab.backend.catalog.dto.ProductResponse;
import com.retaillab.backend.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lists products, paginated, optionally filtered by category.
     * Examples:
     *   GET /api/products
     *   GET /api/products?page=1&size=20
     *   GET /api/products?categoryId=3
     */
    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productService.list(categoryId, pageable);
    }

    /**
     * Returns a single product by id.
     * GET /api/products/42
     */
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }
}
