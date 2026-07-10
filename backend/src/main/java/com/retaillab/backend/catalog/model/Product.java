package com.retaillab.backend.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A sellable product. Price is intentionally NOT a plain field here.
 * It is modeled as a history through {@link Price}, since retail prices
 * change over time and we want to keep track of when each price was valid,
 * not just overwrite the current one.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Price> prices = new ArrayList<>();

    /**
     * Convenience method, not persisted. Returns the price currently in effect,
     * i.e. the one with no end date (or the most recent one), if any.
     */
    @Transient
    public Price getCurrentPrice() {
        return prices.stream()
                .filter(p -> p.getEndDate() == null)
                .findFirst()
                .orElse(null);
    }
}
