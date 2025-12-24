package com.ecommerce.customer.exception;

/**
 * Exception thrown when a duplicate resource is detected.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
