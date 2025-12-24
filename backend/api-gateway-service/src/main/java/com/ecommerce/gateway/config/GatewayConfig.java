package com.ecommerce.gateway.config;

import com.ecommerce.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for API Gateway routes with JWT authentication.
 * Defines routes to backend services and applies authentication filters.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configure routes with JWT authentication filter.
     * Public endpoints (auth, GET products/categories) don't require authentication.
     *
     * @param builder the route locator builder
     * @return configured route locator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Apply JWT filter to all routes
                .route("customer-auth", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${CUSTOMER_SERVICE_URL:http://localhost:8083}"))
                
                .route("customer-service", r -> r.path("/api/v1/customers/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${CUSTOMER_SERVICE_URL:http://localhost:8083}"))
                
                .route("address-service", r -> r.path("/api/v1/addresses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${CUSTOMER_SERVICE_URL:http://localhost:8083}"))
                
                .route("product-service", r -> r.path("/api/v1/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${PRODUCT_SERVICE_URL:http://localhost:8081}"))
                
                .route("category-service", r -> r.path("/api/v1/categories/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${PRODUCT_SERVICE_URL:http://localhost:8081}"))
                
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("${ORDER_SERVICE_URL:http://localhost:8082}"))
                
                .build();
    }
}
