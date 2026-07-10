package com.retaillab.backend.catalog.controller;

import com.retaillab.backend.catalog.dto.CategoryResponse;
import com.retaillab.backend.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    /**
     * Small, unpaginated list, categories are expected to stay few in number.
     * Mainly used by the frontend to populate filter dropdowns.
     */
    @GetMapping
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }
}
