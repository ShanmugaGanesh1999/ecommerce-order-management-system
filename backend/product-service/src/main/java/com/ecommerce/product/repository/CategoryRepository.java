package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Provides CRUD operations and custom queries.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by name (case-insensitive).
     *
     * @param name the category name
     * @return optional category
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Check if a category with the given name exists.
     *
     * @param name the category name
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if a category with the given name exists excluding a specific category ID.
     *
     * @param name the category name
     * @param id the category ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
