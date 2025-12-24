package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Product entity.
 * Provides CRUD operations and custom queries with Spring Data Specifications support.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Find products by name containing the given text (case-insensitive).
     *
     * @param name the name to search for
     * @return list of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find all products in a specific category.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find products by a list of IDs.
     *
     * @param ids the list of product IDs
     * @return list of products
     */
    List<Product> findByIdIn(List<Long> ids);

    /**
     * Find all active products.
     *
     * @return list of active products
     */
    List<Product> findByIsActiveTrue();

    /**
     * Check if a product with the given SKU exists.
     *
     * @param sku the SKU to check
     * @return true if exists, false otherwise
     */
    boolean existsBySku(String sku);

    /**
     * Check if a product with the given SKU exists excluding a specific product ID.
     *
     * @param sku the SKU to check
     * @param id the product ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsBySkuAndIdNot(String sku, Long id);
}
