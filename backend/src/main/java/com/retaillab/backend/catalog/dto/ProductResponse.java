package com.retaillab.backend.catalog.dto;

import java.math.BigDecimal;

/**
 * What the API exposes for a product. Deliberately not the JPA entity itself,
 * so we control what goes over the wire and avoid lazy-loading surprises.
 */
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Integer stock,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        String currency
) {
}
