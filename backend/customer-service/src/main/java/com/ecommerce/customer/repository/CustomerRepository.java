package com.ecommerce.customer.repository;

import com.ecommerce.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Customer entity.
 * Provides database operations for customer management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by email address.
     *
     * @param email the email to search for
     * @return an Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Check if a customer exists with the given email.
     *
     * @param email the email to check
     * @return true if a customer with this email exists
     */
    boolean existsByEmail(String email);
}
