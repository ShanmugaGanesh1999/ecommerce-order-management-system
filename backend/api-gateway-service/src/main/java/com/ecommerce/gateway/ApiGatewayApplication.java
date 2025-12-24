package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for API Gateway Service.
 * Routes requests to appropriate microservices and handles cross-cutting concerns.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
