package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductDTO;
import com.ecommerce.order.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for communicating with the Product Service.
 * Handles product validation and information retrieval.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    /**
     * Get product information by ID from Product Service.
     *
     * @param productId the product ID
     * @return product DTO
     * @throws ResourceNotFoundException if product not found
     */
    public ProductDTO getProduct(Long productId) {
        String url = productServiceUrl + "/api/v1/products/" + productId;
        log.debug("Calling Product Service: {}", url);

        try {
            ProductDTO product = restTemplate.getForObject(url, ProductDTO.class);
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + productId);
            }
            return product;
        } catch (HttpClientErrorException.NotFound ex) {
            log.error("Product not found with id: {}", productId);
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        } catch (Exception ex) {
            log.error("Error calling Product Service: {}", ex.getMessage());
            throw new RuntimeException("Failed to retrieve product information: " + ex.getMessage());
        }
    }

    /**
     * Validate product availability for order.
     * Checks if product exists, is active, and has sufficient stock.
     *
     * @param productId the product ID
     * @param quantity the quantity requested
     * @throws ResourceNotFoundException if product not found
     * @throws IllegalArgumentException if product is inactive or insufficient stock
     */
    public void validateProductAvailability(Long productId, Integer quantity) {
        ProductDTO product = getProduct(productId);

        if (product.getIsActive() == null || !product.getIsActive()) {
            throw new IllegalArgumentException("Product with id " + productId + " is not active");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product id " + productId +
                    ". Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
    }
}
