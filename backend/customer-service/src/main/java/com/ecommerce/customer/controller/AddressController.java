package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.AddressRequest;
import com.ecommerce.customer.dto.AddressResponse;
import com.ecommerce.customer.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for address operations.
 * Handles customer address management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Address management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    /**
     * Get all addresses for a customer.
     *
     * @param customerId the customer ID
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return page of addresses
     */
    @GetMapping("/api/v1/customers/{customerId}/addresses")
    @Operation(summary = "Get customer addresses", description = "Get all addresses for a customer with pagination")
    public ResponseEntity<Page<AddressResponse>> getCustomerAddresses(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AddressResponse> addresses = addressService.getCustomerAddresses(customerId, pageable);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Create a new address for a customer.
     *
     * @param customerId the customer ID
     * @param request address request
     * @return created address
     */
    @PostMapping("/api/v1/customers/{customerId}/addresses")
    @Operation(summary = "Create address", description = "Create a new address for a customer")
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(customerId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing address.
     *
     * @param addressId the address ID
     * @param customerId the customer ID
     * @param request address request
     * @return updated address
     */
    @PutMapping("/api/v1/addresses/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestParam Long customerId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.updateAddress(addressId, customerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an address.
     *
     * @param addressId the address ID
     * @param customerId the customer ID
     * @return no content
     */
    @DeleteMapping("/api/v1/addresses/{addressId}")
    @Operation(summary = "Delete address", description = "Delete an address")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            @RequestParam Long customerId) {
        addressService.deleteAddress(addressId, customerId);
        return ResponseEntity.noContent().build();
    }
}
