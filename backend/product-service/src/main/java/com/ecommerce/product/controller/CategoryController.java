package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.dto.CreateCategoryRequest;
import com.ecommerce.product.dto.UpdateCategoryRequest;
import com.ecommerce.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category management.
 * Provides endpoints for category CRUD operations.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories.
     *
     * @return list of all categories
     */
    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all product categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("GET /api/v1/categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return category details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get a single category by its ID")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("GET /api/v1/categories/{}", id);
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Create a new category.
     * Admin only operation.
     *
     * @param request the create category request
     * @return created category
     */
    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category (Admin only)")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("POST /api/v1/categories - name: {}", request.getName());
        CategoryDTO category = categoryService.createCategory(request);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    /**
     * Update an existing category.
     * Admin only operation.
     *
     * @param id the category ID
     * @param request the update category request
     * @return updated category
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category (Admin only)")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("PUT /api/v1/categories/{}", id);
        CategoryDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(category);
    }

    /**
     * Delete a category.
     * Admin only operation.
     *
     * @param id the category ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category (Admin only)")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/v1/categories/{}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
