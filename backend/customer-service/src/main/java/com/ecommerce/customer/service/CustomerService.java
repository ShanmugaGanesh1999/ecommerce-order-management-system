package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.dto.UpdateCustomerRequest;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.exception.ResourceNotFoundException;
import com.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for customer operations.
 * Handles customer profile management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Get customer by ID.
     *
     * @param customerId the customer ID
     * @return customer response
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        log.debug("Fetching customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        return mapToResponse(customer);
    }

    /**
     * Update customer profile.
     *
     * @param customerId the customer ID
     * @param request update request
     * @return updated customer response
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        // Update fields if provided
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        customer = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", customerId);

        return mapToResponse(customer);
    }

    /**
     * Map customer entity to response DTO.
     *
     * @param customer the customer entity
     * @return customer response DTO
     */
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .role(customer.getRole())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
