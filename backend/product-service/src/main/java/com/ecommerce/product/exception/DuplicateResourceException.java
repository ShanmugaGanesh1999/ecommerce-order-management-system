package com.ecommerce.product.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
