package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.dto.UpdateCustomerRequest;
import com.ecommerce.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for customer profile operations.
 * Handles customer profile retrieval and updates.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Get customer profile by ID.
     *
     * @param customerId the customer ID
     * @return customer response
     */
    @GetMapping("/{customerId}/profile")
    @Operation(summary = "Get customer profile", description = "Get customer profile information by ID")
    public ResponseEntity<CustomerResponse> getProfile(@PathVariable Long customerId) {
        CustomerResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer profile.
     *
     * @param customerId the customer ID
     * @param request update request
     * @return updated customer response
     */
    @PutMapping("/{customerId}/profile")
    @Operation(summary = "Update customer profile", description = "Update customer profile information")
    public ResponseEntity<CustomerResponse> updateProfile(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }
}
