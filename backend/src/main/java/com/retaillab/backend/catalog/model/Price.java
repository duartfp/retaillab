package com.retaillab.backend.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A price entry for a product, valid for a period of time.
 * endDate null means this is the currently active price.
 * This mirrors how retail systems track price changes historically
 * instead of just overwriting a single "price" column.
 */
@Entity
@Table(name = "price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "BRL";

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * Null while this price is the active one.
     */
    private LocalDate endDate;
}
