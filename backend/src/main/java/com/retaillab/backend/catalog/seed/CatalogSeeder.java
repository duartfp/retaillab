package com.retaillab.backend.catalog.seed;

import com.retaillab.backend.catalog.model.Category;
import com.retaillab.backend.catalog.model.Price;
import com.retaillab.backend.catalog.model.Product;
import com.retaillab.backend.catalog.repository.CategoryRepository;
import com.retaillab.backend.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Populates the database with fake but realistic retail data on startup,
 * only if the product table is still empty. Safe to run every time the
 * application starts, it never duplicates data.
 * <p>
 * This exists purely for local development and demos, it is not meant
 * to run in production (see application.properties profiles later on).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Catalog already seeded, skipping ({} products found)", productRepository.count());
            return;
        }

        log.info("Seeding catalog with fake retail data...");

        Category electronics = category("Electronics", "Gadgets, computers and accessories");
        Category apparel = category("Apparel", "Clothing and footwear");
        Category home = category("Home & Kitchen", "Furniture, appliances and kitchenware");
        Category books = category("Books", "Fiction, non-fiction and technical books");
        Category sports = category("Sporting Goods", "Equipment and gear for sports and outdoors");

        List<SeedProduct> seedProducts = List.of(
                new SeedProduct("ELEC-001", "Wireless Mechanical Keyboard", "75% layout, hot-swappable switches", 40, new BigDecimal("459.90"), electronics),
                new SeedProduct("ELEC-002", "27-inch 4K Monitor", "IPS panel, 144Hz refresh rate", 15, new BigDecimal("2199.00"), electronics),
                new SeedProduct("ELEC-003", "Noise Cancelling Headphones", "Over-ear, 30h battery life", 60, new BigDecimal("899.90"), electronics),
                new SeedProduct("ELEC-004", "USB-C Hub, 7-in-1", "HDMI, SD card, 3x USB-A, PD passthrough", 120, new BigDecimal("189.90"), electronics),
                new SeedProduct("APRL-001", "Merino Wool Sweater", "Navy blue, unisex, sizes S to XL", 80, new BigDecimal("349.00"), apparel),
                new SeedProduct("APRL-002", "Running Shoes", "Lightweight, breathable mesh", 95, new BigDecimal("429.90"), apparel),
                new SeedProduct("APRL-003", "Denim Jacket", "Classic fit, mid-wash", 50, new BigDecimal("299.00"), apparel),
                new SeedProduct("HOME-001", "Espresso Machine", "15-bar pump, milk frother included", 25, new BigDecimal("1349.00"), home),
                new SeedProduct("HOME-002", "Cast Iron Skillet, 12-inch", "Pre-seasoned, oven safe", 70, new BigDecimal("219.90"), home),
                new SeedProduct("HOME-003", "Standing Desk, Electric", "Adjustable height, memory presets", 18, new BigDecimal("1899.00"), home),
                new SeedProduct("BOOK-001", "Clean Code", "Robert C. Martin, software craftsmanship", 200, new BigDecimal("89.90"), books),
                new SeedProduct("BOOK-002", "The Pragmatic Programmer", "20th anniversary edition", 150, new BigDecimal("99.90"), books),
                new SeedProduct("BOOK-003", "Designing Data-Intensive Applications", "Martin Kleppmann", 90, new BigDecimal("149.90"), books),
                new SeedProduct("SPRT-001", "Adjustable Dumbbell Set", "5 to 25kg per side", 30, new BigDecimal("1199.00"), sports),
                new SeedProduct("SPRT-002", "Yoga Mat, Extra Thick", "6mm, non-slip surface", 140, new BigDecimal("119.90"), sports),
                new SeedProduct("SPRT-003", "Insulated Water Bottle", "1 liter, keeps cold 24h", 300, new BigDecimal("89.00"), sports)
        );

        seedProducts.forEach(this::createProductWithPrice);

        log.info("Catalog seeded: {} categories, {} products", 5, seedProducts.size());
    }

    private Category category(String name, String description) {
        return categoryRepository.save(
                Category.builder()
                        .name(name)
                        .description(description)
                        .build()
        );
    }

    private void createProductWithPrice(SeedProduct seed) {
        Product product = Product.builder()
                .sku(seed.sku())
                .name(seed.name())
                .description(seed.description())
                .stock(seed.stock())
                .category(seed.category())
                .build();

        Price price = Price.builder()
                .product(product)
                .amount(seed.price())
                .currency("BRL")
                .startDate(LocalDate.now())
                .build();

        product.getPrices().add(price);
        productRepository.save(product);
    }

    private record SeedProduct(
            String sku,
            String name,
            String description,
            Integer stock,
            BigDecimal price,
            Category category
    ) {
    }
}
