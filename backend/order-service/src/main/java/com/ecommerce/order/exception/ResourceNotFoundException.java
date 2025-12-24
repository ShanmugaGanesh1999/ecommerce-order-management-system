package com.ecommerce.order.exception;

/**
 * Exception thrown when a requested resource is not found.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
