package com.ecommerce.product.service;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.dto.CreateCategoryRequest;
import com.ecommerce.product.dto.UpdateCategoryRequest;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.exception.DuplicateResourceException;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category management.
 * Handles business logic for category CRUD operations.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories.
     *
     * @return list of category DTOs
     */
    public List<CategoryDTO> getAllCategories() {
        log.debug("Getting all categories");
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return category DTO
     */
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Getting category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    /**
     * Create a new category.
     *
     * @param request the create category request
     * @return created category DTO
     */
    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        log.info("Creating new category with name: {}", request.getName());

        // Check if category with same name already exists
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category with name " + request.getName() + " already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());
        return convertToDTO(savedCategory);
    }

    /**
     * Update an existing category.
     *
     * @param id the category ID
     * @param request the update category request
     * @return updated category DTO
     */
    @Transactional
    public CategoryDTO updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            // Check if another category with same name exists
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
                throw new DuplicateResourceException("Category with name " + request.getName() + " already exists");
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());
        return convertToDTO(updatedCategory);
    }

    /**
     * Delete a category.
     *
     * @param id the category ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }

    /**
     * Convert Category entity to CategoryDTO.
     *
     * @param category the category entity
     * @return category DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
