package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.AddressRequest;
import com.ecommerce.customer.dto.AddressResponse;
import com.ecommerce.customer.entity.Address;
import com.ecommerce.customer.exception.ResourceNotFoundException;
import com.ecommerce.customer.repository.AddressRepository;
import com.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for address operations.
 * Handles customer address management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    /**
     * Get all addresses for a customer.
     *
     * @param customerId the customer ID
     * @param pageable pagination information
     * @return page of addresses
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional(readOnly = true)
    public Page<AddressResponse> getCustomerAddresses(Long customerId, Pageable pageable) {
        log.debug("Fetching addresses for customer ID: {}", customerId);

        // Verify customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        return addressRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Create a new address for a customer.
     *
     * @param customerId the customer ID
     * @param request address request
     * @return created address
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional
    public AddressResponse createAddress(Long customerId, AddressRequest request) {
        log.info("Creating new address for customer ID: {}", customerId);

        // Verify customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }

        // If this is set as default, remove default from other addresses
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByCustomerIdAndIsDefaultTrue(customerId)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        addressRepository.save(existingDefault);
                    });
        }

        Address address = Address.builder()
                .customerId(customerId)
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .isDefault(request.getIsDefault())
                .build();

        address = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", address.getId());

        return mapToResponse(address);
    }

    /**
     * Update an existing address.
     *
     * @param addressId the address ID
     * @param customerId the customer ID (for authorization)
     * @param request address request
     * @return updated address
     * @throws ResourceNotFoundException if address not found
     */
    @Transactional
    public AddressResponse updateAddress(Long addressId, Long customerId, AddressRequest request) {
        log.info("Updating address ID: {} for customer ID: {}", addressId, customerId);

        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId + " for customer: " + customerId));

        // If this is set as default, remove default from other addresses
        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            addressRepository.findByCustomerIdAndIsDefaultTrue(customerId)
                    .ifPresent(existingDefault -> {
                        if (!existingDefault.getId().equals(addressId)) {
                            existingDefault.setIsDefault(false);
                            addressRepository.save(existingDefault);
                        }
                    });
        }

        // Update fields
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setIsDefault(request.getIsDefault());

        address = addressRepository.save(address);
        log.info("Address updated successfully with ID: {}", addressId);

        return mapToResponse(address);
    }

    /**
     * Delete an address.
     *
     * @param addressId the address ID
     * @param customerId the customer ID (for authorization)
     * @throws ResourceNotFoundException if address not found
     */
    @Transactional
    public void deleteAddress(Long addressId, Long customerId) {
        log.info("Deleting address ID: {} for customer ID: {}", addressId, customerId);

        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId + " for customer: " + customerId));

        addressRepository.delete(address);
        log.info("Address deleted successfully with ID: {}", addressId);
    }

    /**
     * Map address entity to response DTO.
     *
     * @param address the address entity
     * @return address response DTO
     */
    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .customerId(address.getCustomerId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
