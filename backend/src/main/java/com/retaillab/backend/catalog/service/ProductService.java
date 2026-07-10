package com.retaillab.backend.catalog.service;

import com.retaillab.backend.catalog.dto.ProductResponse;
import com.retaillab.backend.catalog.model.Price;
import com.retaillab.backend.catalog.model.Product;
import com.retaillab.backend.catalog.repository.ProductRepository;
import com.retaillab.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> list(Long categoryId, Pageable pageable) {
        Page<Product> products = (categoryId != null)
                ? productRepository.findByCategoryId(categoryId, pageable)
                : productRepository.findAll(pageable);

        return products.map(this::toResponse);
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found, id " + id));
        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        Price currentPrice = product.getCurrentPrice();

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                currentPrice != null ? currentPrice.getAmount() : null,
                currentPrice != null ? currentPrice.getCurrency() : null
        );
    }
}
