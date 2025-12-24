package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.UpdateProductRequest;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product management.
 * Provides endpoints for product CRUD operations, search, and filtering.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products with optional filters and pagination.
     *
     * @param keyword search keyword for product name
     * @param categoryId filter by category ID
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @param inStock filter for in-stock products
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @param sortBy field to sort by (default "id")
     * @param sortDir sort direction (default "asc")
     * @return page of products
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Get all products with pagination, filtering and sorting")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /api/v1/products - keyword: {}, categoryId: {}, page: {}, size: {}",
                keyword, categoryId, page, size);

        Page<ProductDTO> products = productService.getAllProducts(
                keyword, categoryId, minPrice, maxPrice, inStock, page, size, sortBy, sortDir);
        return ResponseEntity.ok(products);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return product details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get a single product by its ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("GET /api/v1/products/{}", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Search products by keyword.
     *
     * @param keyword the search keyword
     * @return list of matching products
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by keyword in name")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        log.info("GET /api/v1/products/search?keyword={}", keyword);
        List<ProductDTO> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category ID.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Get all products in a specific category")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("GET /api/v1/products/category/{}", categoryId);
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Create a new product.
     * Admin only operation.
     *
     * @param request the create product request
     * @return created product
     */
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/v1/products - name: {}, sku: {}", request.getName(), request.getSku());
        ProductDTO product = productService.createProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    /**
     * Update an existing product.
     * Admin only operation.
     *
     * @param id the product ID
     * @param request the update product request
     * @return updated product
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("PUT /api/v1/products/{}", id);
        ProductDTO product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    /**
     * Soft delete a product.
     * Admin only operation.
     *
     * @param id the product ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft delete a product (Admin only)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
