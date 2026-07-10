package com.retaillab.backend.catalog.repository;

import com.retaillab.backend.catalog.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Optional<Price> findByProductIdAndEndDateIsNull(Long productId);

    List<Price> findByProductIdOrderByStartDateDesc(Long productId);
}
