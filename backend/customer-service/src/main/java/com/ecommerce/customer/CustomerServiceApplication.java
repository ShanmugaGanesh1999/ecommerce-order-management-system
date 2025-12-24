package com.ecommerce.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Customer Service.
 * Handles customer authentication, profile management, and address management.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class CustomerServiceApplication {

    /**
     * Main entry point for the Customer Service application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
