package com.ecommerce.order.exception;

/**
 * Exception thrown when an invalid operation is attempted.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
