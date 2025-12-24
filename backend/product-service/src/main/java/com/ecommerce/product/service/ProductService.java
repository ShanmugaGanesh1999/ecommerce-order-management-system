package com.ecommerce.product.service;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.DuplicateResourceException;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Product management.
 * Handles business logic for product operations including CRUD, filtering, and search.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Get all products with pagination, filtering, and sorting.
     *
     * @param keyword search keyword for product name
     * @param categoryId filter by category ID
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @param inStock filter for in-stock products
     * @param page page number
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDir sort direction (asc/desc)
     * @return page of product DTOs
     */
    public Page<ProductDTO> getAllProducts(String keyword, Long categoryId, BigDecimal minPrice,
                                           BigDecimal maxPrice, Boolean inStock, int page, int size,
                                           String sortBy, String sortDir) {
        log.debug("Getting products - keyword: {}, categoryId: {}, minPrice: {}, maxPrice: {}, inStock: {}, page: {}, size: {}",
                keyword, categoryId, minPrice, maxPrice, inStock, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = Specification.where(null);

        // Build dynamic specifications
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(ProductSpecifications.nameContains(keyword));
        }
        if (categoryId != null) {
            spec = spec.and(ProductSpecifications.hasCategoryId(categoryId));
        }
        if (minPrice != null) {
            spec = spec.and(ProductSpecifications.priceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ProductSpecifications.priceLessThanOrEqual(maxPrice));
        }
        if (inStock != null && inStock) {
            spec = spec.and(ProductSpecifications.isInStock());
        }
        spec = spec.and(ProductSpecifications.isActive());

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::convertToDTO);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return product DTO
     */
    public ProductDTO getProductById(Long id) {
        log.debug("Getting product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    /**
     * Search products by keyword.
     *
     * @param keyword the search keyword
     * @return list of product DTOs
     */
    public List<ProductDTO> searchProducts(String keyword) {
        log.debug("Searching products with keyword: {}", keyword);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category ID.
     *
     * @param categoryId the category ID
     * @return list of product DTOs
     */
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        log.debug("Getting products by category id: {}", categoryId);
        // Verify category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new product.
     *
     * @param request the create product request
     * @return created product DTO
     */
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());

        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists");
        }

        // Verify category exists
        categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .categoryId(request.getCategoryId())
                .imageUrl(request.getImageUrl())
                .sku(request.getSku())
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return convertToDTO(savedProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id the product ID
     * @param request the update product request
     * @return updated product DTO
     */
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getCategoryId() != null) {
            // Verify category exists
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        return convertToDTO(updatedProduct);
    }

    /**
     * Soft delete a product by setting isActive to false.
     *
     * @param id the product ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product soft deleted successfully with id: {}", id);
    }

    /**
     * Convert Product entity to ProductDTO.
     *
     * @param product the product entity
     * @return product DTO
     */
    private ProductDTO convertToDTO(Product product) {
        String categoryName = null;
        if (product.getCategoryId() != null) {
            categoryName = categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName)
                    .orElse(null);
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Inner class for Product Specifications used in dynamic queries.
     */
    private static class ProductSpecifications {

        public static Specification<Product> nameContains(String keyword) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                            "%" + keyword.toLowerCase() + "%");
        }

        public static Specification<Product> hasCategoryId(Long categoryId) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoryId"), categoryId);
        }

        public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        }

        public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        }

        public static Specification<Product> isInStock() {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("stockQuantity"), 0);
        }

        public static Specification<Product> isActive() {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("isActive"), true);
        }
    }
}
