package com.ecommerce.customer.repository;

import com.ecommerce.customer.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Address entity.
 * Provides database operations for address management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Find all addresses for a customer with pagination.
     *
     * @param customerId the customer ID
     * @param pageable pagination information
     * @return a page of addresses
     */
    Page<Address> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find an address by ID and customer ID.
     *
     * @param id the address ID
     * @param customerId the customer ID
     * @return an Optional containing the address if found
     */
    Optional<Address> findByIdAndCustomerId(Long id, Long customerId);

    /**
     * Find the default address for a customer.
     *
     * @param customerId the customer ID
     * @return an Optional containing the default address if exists
     */
    Optional<Address> findByCustomerIdAndIsDefaultTrue(Long customerId);
}
